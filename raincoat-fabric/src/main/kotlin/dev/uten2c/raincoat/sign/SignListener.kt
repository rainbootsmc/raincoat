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
        val texts = frontText.getMessages(false).map(Text::getString)
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
        listOf(texts[2], texts[3]).forEach { text ->
            when {
                text.startsWith("t/") -> tableId = text.drop(2)
                text.startsWith("b/") -> {
                    val blockId = text.drop(2)
                    replaceBlock = Registries.BLOCK.get(Identifier(blockId))
                }
            }
        }
        return SignObject(ids, replaceBlock ?: Blocks.BARRIER, tableId)
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

    private data class SignLoadContext(val blockEntity: SignBlockEntity, val world: World)
}
