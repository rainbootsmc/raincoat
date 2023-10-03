package dev.uten2c.raincoat.network

import dev.uten2c.raincoat.MOD_ID
import dev.uten2c.raincoat.NamedKey
import dev.uten2c.raincoat.Protocol
import dev.uten2c.raincoat.States
import dev.uten2c.raincoat.debug.DebugShape
import dev.uten2c.raincoat.debug.DebugShapes
import dev.uten2c.raincoat.option.OptionManager
import dev.uten2c.raincoat.shake.ShakeEffect
import dev.uten2c.raincoat.util.PacketId
import dev.uten2c.raincoat.util.StackUtils
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ConfirmLinkScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.math.Vec3d
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.function.Consumer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Networking {
    private val LOGGER = LoggerFactory.getLogger("Raincoat Networking")
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun registerListeners() {
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> States.reset() }
        registerReceiver(Protocol.handshakeRequest) { _, _, _, _ -> onHandshakeRequest() }
        registerReceiver(Protocol.directionSendRequest) { _, _, buf, _ -> onDirectionSendRequest(buf) }
        registerReceiver(Protocol.recoilCamera) { client, _, buf, _ -> onCameraRecoil(client, buf) }
        registerReceiver(Protocol.recoilAnimation) { client, _, buf, _ -> onRecoilAnimation(client, buf) }
        registerReceiver(Protocol.outdated) { _, _, _, _ -> onOutdatedSignal() }
        registerReceiver(Protocol.openUrl) { client, _, buf, _ -> onOpenUrl(client, buf) }
        registerReceiver(Protocol.shapeDisplay) { _, _, buf, _ -> onShapeDisplay(buf) }
        registerReceiver(Protocol.shapeDiscard) { _, _, buf, _ -> onShapeDiscard(buf) }
        registerReceiver(Protocol.shapeClear) { _, _, buf, _ -> onShapeClear(buf) }
        registerReceiver(Protocol.shakePlay) { _, _, buf, _ -> onShakePlay(buf) }
        registerReceiver(Protocol.shakeStop) { _, _, _, _ -> onShakeStop() }
    }

    private fun onHandshakeRequest() {
        States.isHandshakeReceived = true

        States.onJoinServer()
        val metadata = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata
        val version = metadata.version.friendlyString
        val resBuf = PacketByteBufs.create()
        resBuf.writeString(version)
        resBuf.writeVarInt(Protocol.PROTOCOL_VERSION)
        LOGGER.info("Send handshake packet (version: {}, protocol: {})", version, Protocol.PROTOCOL_VERSION)
        send(Protocol.handshakeResponse, resBuf)
        sendSettingsUpdate()
    }

    private fun onDirectionSendRequest(buf: PacketByteBuf) {
        if (buf.readBoolean()) {
            States.directionSendRequestedTime = System.currentTimeMillis()
        } else {
            States.directionSendRequestedTime = null
        }
    }

    private fun onCameraRecoil(client: MinecraftClient, buf: PacketByteBuf) {
        val x = buf.readFloat()
        val y = buf.readFloat()
        val player = client.player
        if (player != null) {
            player.yaw = player.yaw + x
            player.pitch = player.pitch + y
        }
    }

    private fun onRecoilAnimation(client: MinecraftClient, buf: PacketByteBuf) {
        val ticks = buf.readVarInt()
        val player = client.player ?: return
        val stack = player.mainHandStack
        if (!StackUtils.isGun(stack)) {
            return
        }
        States.isRecoiling = true
        scope.launch {
            delay(50L * ticks)
            States.isRecoiling = false
        }
    }

    private fun onOutdatedSignal() {
        States.reset()
    }

    private fun onOpenUrl(client: MinecraftClient, buf: PacketByteBuf) {
        try {
            val currentScreen = client.currentScreen
            val urlString = buf.readString()
            val trusted = buf.readBoolean()
            val uri = URI(urlString)
            if (client.options.chatLinksPrompt.value) {
                client.execute {
                    val confirmScreen = ConfirmLinkScreen(
                        { open -> confirmOpenLink(client, currentScreen, open, uri) },
                        urlString,
                        trusted,
                    )
                    client.setScreen(confirmScreen)
                }
            } else {
                Util.getOperatingSystem().open(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onShapeDisplay(buf: PacketByteBuf) {
        val id = buf.readIdentifier()
        val typeId = buf.readVarInt()
        val debugShape = when (typeId) {
            0 -> DebugShape.Box(buf.readVec3d(), buf.readVec3d(), buf.readVarInt().toUInt())
            1 -> DebugShape.RotateBox(buf.readVec3d(), buf.readVec3d(), buf.readQuaternionf(), buf.readVarInt().toUInt())
            2 -> DebugShape.Message(buf.readVec3d(), buf.readText(), buf.readFloat(), buf.readBoolean())
            else -> throw IllegalStateException()
        }
        DebugShapes.addShape(id, debugShape)
    }

    private fun onShapeDiscard(buf: PacketByteBuf) {
        DebugShapes.removeShape(buf.readIdentifier())
    }

    private fun onShapeClear(buf: PacketByteBuf) {
        val namespace = buf.readNullable { it.readString() }
        if (namespace == null) {
            DebugShapes.clearShapes()
        } else {
            DebugShapes.clearShapesWithNamespace(namespace)
        }
    }

    private fun onShakePlay(buf: PacketByteBuf) {
        val duration = buf.readNullable(PacketByteBuf::readVarLong)?.milliseconds ?: Duration.INFINITE
        val strength = buf.readFloat()
        States.shakeEffect = ShakeEffect(Clock.System.now(), duration, strength)
    }

    private fun onShakeStop() {
        States.shakeEffect = null
    }

    private fun confirmOpenLink(client: MinecraftClient, parentScreen: Screen?, open: Boolean, uri: URI) {
        if (open) {
            Util.getOperatingSystem().open(uri)
        }
        client.setScreen(parentScreen)
    }

    @JvmStatic
    fun sendKeyPressedPacket(key: NamedKey) {
        send(Protocol.keyPressed) { buf: PacketByteBuf -> buf.writeEnumConstant(key) }
    }

    @JvmStatic
    fun sendKeyReleasedPacket(key: NamedKey) {
        send(Protocol.keyReleased) { buf: PacketByteBuf -> buf.writeEnumConstant(key) }
    }

    fun sendDirectionUpdate(yaw: Float, pitch: Float) {
        val requestedTime = States.directionSendRequestedTime ?: return
        send(Protocol.directionUpdate) { buf: PacketByteBuf ->
            buf.writeVarInt((System.currentTimeMillis() - requestedTime).toInt())
            buf.writeFloat(yaw)
            buf.writeFloat(pitch)
        }
    }

    fun sendSettingsUpdate() {
        send(Protocol.settingsUpdate) { buf: PacketByteBuf -> buf.writeBoolean(OptionManager.options.isAdsHold) }
    }

    private fun registerReceiver(id: PacketId, channelHandler: ClientPlayNetworking.PlayChannelHandler) {
        ClientPlayNetworking.registerGlobalReceiver(identifier(id), channelHandler)
    }

    private fun send(id: PacketId, buf: PacketByteBuf) {
        if (States.isOnServer) {
            ClientPlayNetworking.send(identifier(id), buf)
        }
    }

    private fun send(id: PacketId, builder: Consumer<PacketByteBuf>) {
        val buf = PacketByteBufs.create()
        builder.accept(buf)
        send(id, buf)
    }

    private fun sendEmpty(id: PacketId) {
        send(id, PacketByteBufs.empty())
    }

    private fun identifier(id: PacketId): Identifier {
        return Identifier(id.toString())
    }

    private fun PacketByteBuf.readVec3d(): Vec3d {
        return Vec3d(readDouble(), readDouble(), readDouble())
    }
}
