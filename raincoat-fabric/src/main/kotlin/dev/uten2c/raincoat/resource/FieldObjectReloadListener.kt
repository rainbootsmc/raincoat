package dev.uten2c.raincoat.resource

import com.mojang.datafixers.util.Either
import dev.uten2c.raincoat.*
import dev.uten2c.raincoat.mixin.accessor.JsonUnbakedModelAccessor
import dev.uten2c.raincoat.model.Display
import dev.uten2c.raincoat.model.JsonModel
import dev.uten2c.raincoat.model.ModelElement
import dev.uten2c.raincoat.sign.FieldObjectModelMetadata
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
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
import net.minecraft.util.math.Box
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.optionals.getOrNull

object FieldObjectReloadListener : SimpleSynchronousResourceReloadListener {
    private const val BASE_PATH = "rainboots/block"
    private val json = Json {
        ignoreUnknownKeys = true
    }

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
                runCatching {
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

    private fun loadFieldObjects(resourceManager: ResourceManager, executor: Executor): CompletableFuture<LoadContext> {
        return CompletableFuture.supplyAsync(
            {
                FieldObjectModelMetadata.clear()

                val resource = resourceManager.getResource(Identifier("field_objects.txt")).getOrNull() ?: return@supplyAsync null
                val fieldObjects = resource.inputStream.use { input ->
                    input.bufferedReader()
                        .readLines()
                        .filter { it.isNotEmpty() && it.first() != '#' }
                }
                LoadContext(resourceManager, fieldObjects)
            },
            executor,
        )
    }

    private fun replaceModel(loadContext: LoadContext, pluginContext: ModelLoadingPlugin.Context) {
        val fieldObjectList = loadContext.fieldObjectList ?: return

        pluginContext.modifyModelOnLoad().register { model, ctx ->
            val id = ctx.id()

            if (id.namespace == MINECRAFT && id.path == "red_dye") {
                val atomicId = AtomicInteger(1000)
                _idMap = mutableMapOf()
                _itemTabIdMap = mutableMapOf()

                val overrides = mutableListOf<ModelOverride>()
                val missingModelCustomModelData = atomicId.getAndIncrement()
                _idMap["missing"] = missingModelCustomModelData
                overrides.add(
                    ModelOverride(
                        Identifier("$BASE_PATH/missing"),
                        listOf(ModelOverride.Condition(Identifier("custom_model_data"), missingModelCustomModelData.toFloat())),
                    ),
                )

                fieldObjectList.forEach { modelId ->
                    val customModelData = atomicId.getAndIncrement()
                    val condition = ModelOverride.Condition(Identifier("custom_model_data"), customModelData.toFloat())
                    val override = ModelOverride(Identifier("$BASE_PATH/$modelId"), listOf(condition))
                    overrides.add(override)
                    _idMap[pathToId(modelId)] = customModelData
                    _itemTabIdMap[pathToId(modelId)] = customModelData

                    loadContext.resourceManager.getResource(Identifier("models/$BASE_PATH/$modelId.json")).ifPresent {
                        runCatching {
                            val jsonModel = json.decodeFromStream<JsonModel>(it.inputStream)
                            val metadata = getFieldObjectModelMetadata(jsonModel) ?: return@runCatching
                            FieldObjectModelMetadata.put(modelId, metadata)
                            FieldObjectModelMetadata.put(customModelData, metadata)
                        }.onFailure {
                            it.printStackTrace()
                        }
                    }
                }

                return@register JsonUnbakedModel(
                    Identifier("item/generated"),
                    emptyList(),
                    mapOf("layer0" to Either.left(SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier("item/red_dye")))),
                    null,
                    null,
                    ModelTransformation.NONE,
                    overrides,
                )
            }

            if (id.path.startsWith(BASE_PATH)) {
                val modelId = id.path.replace("$BASE_PATH/", "")
                if (modelId !in fieldObjectList) {
                    return@register model
                }

                val resource = loadContext.resourceManager.getResource(Identifier("models/${id.path}.json")).getOrNull() ?: return@register model
                val jsonModel = JsonUnbakedModel.deserialize(resource.inputStream.bufferedReader())
                jsonModel as JsonUnbakedModelAccessor

                val metadata = FieldObjectModelMetadata.getOrNull(modelId) ?: return@register model
                val elements = jsonModel.rawElements
                    .filterIndexed { i, _ -> i !in metadata.removeIndices }

                return@register JsonUnbakedModel(
                    jsonModel.parentId,
                    elements,
                    jsonModel.textureMap,
                    jsonModel.ambientOcclusion,
                    jsonModel.rawGuiLight,
                    jsonModel.rawTransformations,
                    emptyList(),
                )
            }
            return@register model
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
        return path.replace("/", "_")
    }

    private fun getFieldObjectModelMetadata(jsonModel: JsonModel): FieldObjectModelMetadata? {
        val groups = jsonModel.groups ?: return null
        val elements = jsonModel.elements
        if (elements == null) {
            // TODO parent対応
            return null
        }
        val indices = mutableSetOf<Int>()
        val collisionBoxes = mutableSetOf<Box>()
        val interactionBoxes = mutableSetOf<Box>()
        groups
            .asSequence()
            .filterIsInstance<JsonModel.GroupItem.Group>()
            .forEach { group ->
                if (group.name == "collision") {
                    val result = groupToBoxes(elements, group)
                    indices.addAll(result.indices)
                    collisionBoxes.addAll(result.boxes)
                } else if (group.name == "interaction") {
                    val result = groupToBoxes(elements, group)
                    indices.addAll(result.indices)
                    interactionBoxes.addAll(result.boxes)
                }
            }
        return FieldObjectModelMetadata(jsonModel.display?.head ?: Display.IDENTITY, collisionBoxes, interactionBoxes, indices)
    }

    private fun indices(group: JsonModel.GroupItem.Group): List<Int> {
        return group.children.flatMap {
            when (it) {
                is JsonModel.GroupItem.Number -> setOf(it.id)
                is JsonModel.GroupItem.Group -> indices(it)
            }
        }
    }

    private fun groupToBoxes(elements: List<ModelElement>, group: JsonModel.GroupItem.Group): ConvertResult {
        val indices = indices(group)
        val boxes = indices.mapNotNull(elements::getOrNull)
            .map {
                Box(
                    it.from.x.toDouble() / 16 - 0.5, it.from.y.toDouble() / 16, it.from.z.toDouble() / 16 + 0.5,
                    it.to.x.toDouble() / 16 - 0.5, it.to.y.toDouble() / 16, it.to.z.toDouble() / 16 + 0.5,
                )
            }
        return ConvertResult(indices, boxes)
    }

    private data class LoadContext(val resourceManager: ResourceManager, val fieldObjectList: List<String>?)

    private data class ConvertResult(val indices: List<Int>, val boxes: List<Box>)
}
