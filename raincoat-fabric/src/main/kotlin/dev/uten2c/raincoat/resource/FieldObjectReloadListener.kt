package dev.uten2c.raincoat.resource

import dev.uten2c.raincoat.MOD_ID
import dev.uten2c.raincoat.fieldObjectItemGroup
import dev.uten2c.raincoat.shouldUpdateCreativeTab
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.block.entity.SignText
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.resource.ResourceManager
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import kotlin.jvm.optionals.getOrDefault

object FieldObjectReloadListener : SimpleSynchronousResourceReloadListener {
    private const val MODEL_ITEM_PATH = "models/item/red_dye.json"
    private const val BASE_PATH = "rainboots/block/"

    init {
        Registries.ITEM_GROUP.getKey(fieldObjectItemGroup).ifPresent { registryKey ->
            ItemGroupEvents.modifyEntriesEvent(registryKey).register { content ->
                if (!shouldShowItemTab) {
                    return@register
                }
                idMap.forEach { (id, _) ->
                    val messages = arrayOf(Text.of("object"), Text.of(id), ScreenTexts.EMPTY, ScreenTexts.EMPTY)
                    val signText = SignText(messages, messages, DyeColor.BLACK, false)
                    val blockEntityNbt = NbtCompound()
                    SignText.CODEC.encodeStart(NbtOps.INSTANCE, signText).resultOrPartial { }.ifPresent { frontText -> blockEntityNbt.put("front_text", frontText) }
                    SignText.CODEC.encodeStart(NbtOps.INSTANCE, SignText()).resultOrPartial { }.ifPresent { backText -> blockEntityNbt.put("back_text", backText) }
                    blockEntityNbt.putBoolean("is_waxed", false)

                    val lore = NbtList().also { list ->
                        list.add(NbtString.of("\"(+NBT)\""))
                    }
                    val stack = Items.WARPED_SIGN.defaultStack
                    stack.setCustomName(Text.literal(id).setStyle(Style.EMPTY.withItalic(false)))
                    stack.orCreateNbt.put("BlockEntityTag", blockEntityNbt)
                    stack.getOrCreateSubNbt("display").put("Lore", lore)
                    content.add(stack)
                }
            }
        }
    }

    @JvmStatic
    var shouldShowItemTab: Boolean = false
        private set

    @JvmStatic
    var idMap: Map<String, Int> = HashMap()
        private set

    override fun getFabricId(): Identifier {
        return Identifier(MOD_ID, "field_object")
    }

    override fun reload(manager: ResourceManager) {
        shouldShowItemTab = MinecraftClient.getInstance().resourcePackManager.enabledProfiles
            .any { it.name != "server" && it.description.contains(Text.of("RainbootsMC.net")) }

        manager.getResource(Identifier(MODEL_ITEM_PATH)).ifPresent { resource ->
            resource.inputStream.use { input ->
                val string = input.readAllBytes().toString(Charsets.UTF_8)
                val model = JsonUnbakedModel.deserialize(string)
                idMap = model.overrides
                    .filter { it.modelId.path.startsWith(BASE_PATH) }
                    .associate {
                        val modelId = it.modelId.path
                            .replace(BASE_PATH, "")
                            .replace("/", "_")
                        val customModelData = it.streamConditions()
                            .filter { c -> c.type.path == "custom_model_data" }
                            .map { c -> c.threshold.toInt() }
                            .findFirst()
                            .getOrDefault(0)
                        modelId to customModelData
                    }
            }
        }

        shouldUpdateCreativeTab = true
    }
}
