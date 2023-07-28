package dev.uten2c.raincoat.sign

import kotlinx.coroutines.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.concurrent.ConcurrentHashMap

object SignListener {
    private val entityMap = HashMap<SignBlockEntity, Entity>()
    private val signObjectMap = HashMap<SignBlockEntity, SignObject>()
    private val loadedQueue = ConcurrentHashMap.newKeySet<SignLoadContext>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun register() {
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register { blockEntity, world ->
            if (blockEntity is SignBlockEntity) {
                scope.launch {
                    delay(50L)
                    loadedQueue.add(SignLoadContext(blockEntity, world))
                }
            }
        }

        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register { blockEntity, world ->
            if (blockEntity is SignBlockEntity) {
                onUnload(blockEntity, world)
            }
        }

        ClientTickEvents.END_CLIENT_TICK.register {
            loadedQueue.removeAll {
                onLoad(it.blockEntity, it.world)
                true
            }
        }
    }

    private fun onLoad(blockEntity: SignBlockEntity, world: World) {
        updateEntity(blockEntity, world)
    }

    private fun onUnload(blockEntity: SignBlockEntity, world: World) {
        removeEntity(blockEntity)
    }

    fun onUpdate(blockEntity: SignBlockEntity, world: World) {
        updateEntity(blockEntity, world)
    }

    private fun removeEntity(blockEntity: SignBlockEntity) {
        signObjectMap.remove(blockEntity)
        val entity = entityMap.remove(blockEntity) ?: return
        val client = MinecraftClient.getInstance()
        val networkHandler = client.networkHandler ?: return
        networkHandler.onEntitiesDestroy(EntitiesDestroyS2CPacket(entity.id))
    }

    private fun updateEntity(blockEntity: SignBlockEntity, world: World) {
        val frontText = blockEntity.frontText
        val frontTexts = frontText.getMessages(false).map(Text::getString)
        val backTexts = blockEntity.backText.getMessages(false).map(Text::getString)
        val texts = mutableListOf<String>()
        texts.addAll(frontTexts)
        texts.addAll(backTexts)
        val signObject = parseSignObject(texts)
        if (signObject == null) {
            signObjectMap.remove(blockEntity)
            return
        } else {
            signObjectMap[blockEntity] = signObject
        }
    }

    private fun parseSignObject(texts: List<String>): SignObject? {
        if (!texts[0].equals("object", true)) {
            return null
        }

        val ids = parseModelIds(texts[1]) ?: return null
        var tableId: String? = null
        var replaceBlock: Block? = null
        var bbStart: Vec3d? = null
        var bbEnd: Vec3d? = null
        var bbDisplay = false
        texts.drop(2)
            .map { it.split("/") }
            .filter { it.size == 2 }
            .map { it[0] to it[1] }
            .forEach { (key, value) ->
                when (key) {
                    "t" -> tableId = value
                    "b" -> replaceBlock = Registries.BLOCK.get(Identifier(value))
                    "bbs" -> bbStart = parseVec3d(value)
                    "bbe" -> bbEnd = parseVec3d(value)
                    "bbd" -> bbDisplay = value.toBooleanStrictOrNull() ?: false
                }
            }
        return SignObject(
            ids,
            replaceBlock ?: Blocks.BARRIER,
            tableId,
            bbStart ?: Vec3d(-0.5, -0.5, -0.5),
            bbEnd ?: Vec3d(0.5, 0.5, 0.5),
            bbDisplay,
        )
    }

    private fun parseModelIds(string: String): List<Int>? {
        val idOrNullList = string.split("/")
            .map { it.toIntOrNull() }

        if (null in idOrNullList) {
            return null
        }

        return idOrNullList.filterNotNull()
    }

    @JvmStatic
    fun getCachedSignObject(blockEntity: SignBlockEntity): SignObject? {
        return signObjectMap[blockEntity]
    }

    private fun parseVec3d(string: String): Vec3d? {
        val values = string.split(",")
            .map { it.toDoubleOrNull() }
        if (null in values || values.size != 3) {
            return null
        }
        return Vec3d(values[0]!!, values[1]!!, values[2]!!)
    }

    private data class SignLoadContext(val blockEntity: SignBlockEntity, val world: World)
}
