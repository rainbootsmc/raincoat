package dev.uten2c.raincoat.network

import dev.uten2c.raincoat.MOD_ID
import dev.uten2c.raincoat.NamedKey
import dev.uten2c.raincoat.Protocol
import dev.uten2c.raincoat.States
import dev.uten2c.raincoat.option.OptionManager
import dev.uten2c.raincoat.util.PacketId
import dev.uten2c.raincoat.util.now
import kotlinx.datetime.Instant
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import java.util.function.Consumer
import kotlin.time.Duration.Companion.seconds

object Networking {
    private val LOGGER = LoggerFactory.getLogger("Raincoat Networking")
    private var handshakeRequestedTime = Instant.DISTANT_PAST

    fun registerListeners() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ -> onJoin() }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> States.reset() }
        registerReceiver(Protocol.HANDSHAKE_REQUEST) { _, _, _, _ -> onHandshakeRequest() }
        registerReceiver(Protocol.DIRECTION_SEND_REQUEST) { _, _, buf, _ -> onDirectionSendRequest(buf) }
        registerReceiver(Protocol.RECOIL) { client, _, buf, _ -> onRecoil(client, buf) }
        registerReceiver(Protocol.OUTDATED) { _, _, _, _ -> onOutdatedSignal() }
    }

    private fun onJoin() {
        if (handshakeRequestedTime != Instant.DISTANT_PAST && now() - handshakeRequestedTime > 20.seconds) {
            return
        }
        States.onJoinServer()
        val metadata = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata
        val version = metadata.version.friendlyString
        val resBuf = PacketByteBufs.create()
        resBuf.writeString(version)
        resBuf.writeVarInt(Protocol.PROTOCOL_VERSION)
        LOGGER.info("Send handshake packet (version: {}, protocol: {})", version, Protocol.PROTOCOL_VERSION)
        send(Protocol.HANDSHAKE_RESPONSE, resBuf)
        sendSettingsUpdate()
    }

    private fun onHandshakeRequest() {
        println("REQUESTED")
        handshakeRequestedTime = now()
        States.isHandshakeReceived = true
    }

    private fun onDirectionSendRequest(buf: PacketByteBuf) {
        if (buf.readBoolean()) {
            States.directionSendRequestedTime = System.currentTimeMillis()
        } else {
            States.directionSendRequestedTime = null
        }
    }

    private fun onRecoil(client: MinecraftClient, buf: PacketByteBuf) {
        val x = buf.readFloat()
        val y = buf.readFloat()
        val player = client.player
        if (player != null) {
            player.yaw = player.yaw + x
            player.pitch = player.pitch + y
        }
    }

    private fun onOutdatedSignal() {
        States.reset()
    }

    @JvmStatic
    fun sendKeyPressedPacket(key: NamedKey) {
        send(Protocol.KEY_PRESSED) { buf: PacketByteBuf -> buf.writeEnumConstant(key) }
    }

    @JvmStatic
    fun sendKeyReleasedPacket(key: NamedKey) {
        send(Protocol.KEY_RELEASED) { buf: PacketByteBuf -> buf.writeEnumConstant(key) }
    }

    fun sendDirectionUpdate(yaw: Float, pitch: Float) {
        val requestedTime = States.directionSendRequestedTime ?: return
        send(Protocol.DIRECTION_UPDATE) { buf: PacketByteBuf ->
            buf.writeVarInt((System.currentTimeMillis() - requestedTime).toInt())
            buf.writeFloat(yaw)
            buf.writeFloat(pitch)
        }
    }

    fun sendSettingsUpdate() {
        send(Protocol.SETTINGS_UPDATE) { buf: PacketByteBuf -> buf.writeBoolean(OptionManager.options.isAdsHold) }
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
}
