package dev.uten2c.raincoat.resource

import com.mojang.datafixers.util.Either
import dev.uten2c.raincoat.*
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.SignText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.render.model.json.ModelOverride
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.resource.ResourceManager
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.optionals.getOrNull

object FieldObjectReloadListener : SimpleSynchronousResourceReloadListener {
    private const val BASE_PATH = "rainboots/block/"

    init {
        PreparableModelLoadingPlugin.register(::loadFieldObjects, ::replaceModel)

        val barrierGroup = Registries.ITEM_GROUP.getKey(fieldObjectItemGroupBarrier).get()
        val airGroup = Registries.ITEM_GROUP.getKey(fieldObjectItemGroupAir).get()
        registerGroup(barrierGroup, null)
        registerGroup(airGroup, Blocks.AIR)
    }

    @JvmStatic
    var shouldShowItemTab: Boolean = false
        private set

    private var _idMap: MutableMap<String, Int> = HashMap()
    private var _itemTabIdMap: MutableMap<String, Int> = HashMap()

    @JvmStatic
    val idMap: Map<String, Int>
        get() = _idMap

    @JvmStatic
    val itemTabIdMap: Map<String, Int>
        get() = _itemTabIdMap

    override fun getFabricId(): Identifier {
        return Identifier(MOD_ID, "field_object")
    }

    override fun reload(manager: ResourceManager) {
        shouldShowItemTab = MinecraftClient.getInstance().resourcePackManager.enabledProfiles
            .any { it.name != "server" && it.description.contains(Text.of("RainbootsMC.net")) }

        manager.getResource(Identifier("raincoat", "oldid.json")).ifPresent { resource ->
            resource.inputStream.use { input ->
                kotlin.runCatching {
                    val oldIdMap = Json.decodeFromString<Map<String, String>>(input.readAllBytes().toString(Charsets.UTF_8))
                    oldIdMap.mapValues { (_, ref) -> _idMap[pathToId(ref)] }
                        .filterValues { it != null }
                        .forEach { (key, value) ->
                            _idMap[key] = value!!
                        }
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }

        shouldUpdateCreativeTab = true
    }

    private fun loadFieldObjects(resourceManager: ResourceManager, executor: Executor): CompletableFuture<List<String>?> {
        return CompletableFuture.supplyAsync(
            {
                val resource = resourceManager.getResource(Identifier("field_objects.txt")).getOrNull() ?: return@supplyAsync null
                resource.inputStream.use { input ->
                    input.bufferedReader()
                        .readLines()
                        .filter { it.isNotEmpty() && it.first() != '#' }
                }
            },
            executor,
        )
    }

    private fun replaceModel(fieldObjectList: List<String>?, pluginContext: ModelLoadingPlugin.Context) {
        if (fieldObjectList == null) {
            return
        }
        pluginContext.modifyModelOnLoad().register { model, ctx ->
            val id = ctx.id()
            if (id.namespace != MINECRAFT || id.path != "red_dye") {
                return@register model
            }
            val atomicId = AtomicInteger(1000)
            _idMap = mutableMapOf()
            _itemTabIdMap = mutableMapOf()

            val overrides = mutableListOf<ModelOverride>()
            val missingModelCustomModelData = atomicId.getAndIncrement()
            _idMap["missing"] = missingModelCustomModelData
            overrides.add(
                ModelOverride(
                    Identifier("rainboots/block/missing"),
                    listOf(ModelOverride.Condition(Identifier("custom_model_data"), missingModelCustomModelData.toFloat()))
                )
            )

            fieldObjectList.forEach { modelId ->
                val customModelData = atomicId.getAndIncrement()
                val condition = ModelOverride.Condition(Identifier("custom_model_data"), customModelData.toFloat())
                val override = ModelOverride(Identifier(modelId), listOf(condition))
                overrides.add(override)
                _idMap[pathToId(modelId)] = customModelData
                _itemTabIdMap[pathToId(modelId)] = customModelData
            }

            JsonUnbakedModel(
                Identifier("item/generated"),
                emptyList(),
                mapOf("layer0" to Either.left(SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier("item/red_dye")))),
                null,
                null,
                ModelTransformation.NONE,
                overrides,
            )
        }
    }

    private fun registerGroup(groupKey: RegistryKey<ItemGroup>, replaceBlock: Block?) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register { content ->
            if (!shouldShowItemTab) {
                return@register
            }
            itemTabIdMap.forEach { (id, _) ->
                val block = replaceBlock ?: Blocks.BARRIER
                val replaceBlockText = if (replaceBlock == null) ScreenTexts.EMPTY else Text.of("b/${Registries.BLOCK.getId(block).path}")
                val messages = arrayOf(Text.of("object"), Text.of(id), replaceBlockText, ScreenTexts.EMPTY)
                val signText = SignText(messages, messages, DyeColor.BLACK, false)
                val blockEntityNbt = NbtCompound()
                SignText.CODEC.encodeStart(NbtOps.INSTANCE, signText).resultOrPartial { }.ifPresent { frontText -> blockEntityNbt.put("front_text", frontText) }
                SignText.CODEC.encodeStart(NbtOps.INSTANCE, SignText()).resultOrPartial { }.ifPresent { backText -> blockEntityNbt.put("back_text", backText) }
                blockEntityNbt.putBoolean("is_waxed", false)

                val lore = NbtList().also { list ->
                    list.add(NbtString.of("\"(+NBT)\""))
                }
                val stack = Items.WARPED_SIGN.defaultStack
                val name = Text.literal(id).setStyle(Style.EMPTY.withItalic(false))
                    .append(Text.of(" ("))
                    .append(Text.translatable(block.translationKey))
                    .append(Text.of(")"))
                stack.setCustomName(name)
                stack.orCreateNbt.put("BlockEntityTag", blockEntityNbt)
                stack.getOrCreateSubNbt("display").put("Lore", lore)
                content.add(stack)
            }
        }
    }

    private fun pathToId(path: String): String {
        return path
            .replace(BASE_PATH, "")
            .replace("/", "_")
    }
}
