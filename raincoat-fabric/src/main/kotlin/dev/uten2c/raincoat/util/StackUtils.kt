package dev.uten2c.raincoat.util

import dev.uten2c.raincoat.States
import dev.uten2c.raincoat.States.isOnServer
import dev.uten2c.raincoat.resource.ScaleMapReloadListener
import dev.uten2c.raincoat.sign.SignObjectUtils
import dev.uten2c.raincoat.zoom.ZoomLevel
import net.minecraft.block.entity.SignText
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.tag.ItemTags
import net.minecraft.text.Text
import net.minecraft.text.TextContent

object StackUtils {
    private const val IS_GUN_KEY = 0
    private const val CAN_NOT_DROP = 1
    private const val CAN_NOT_CLICK = 2
    private const val NO_SLOT_HIGHLIGHT = 3
    private const val NO_CROSS_HAIR = 4
    private const val NAMESPACE = "rainboots"
    private const val FLAGS = "Flags"
    private const val THIRD_PERSON_STACK = "ThirdPersonStack"
    private const val GUI_STACK = "GuiStack"
    private const val GUN_STATE = "GunState"
    private const val GUN_MODEL_STATE = "GunModelState"
    private const val GUN_RECOIL_MODEL_STATE = "GunRecoilModelState"
    private const val ZOOM_LEVEL = "ZoomLevel"
    private const val EQUIP_DURATION = "EquipDuration"

    @JvmStatic
    fun isGun(stack: ItemStack): Boolean {
        return getFlag(stack, IS_GUN_KEY)
    }

    @JvmStatic
    fun canNotDrop(stack: ItemStack): Boolean {
        return getFlag(stack, CAN_NOT_DROP)
    }

    @JvmStatic
    fun canNotClick(stack: ItemStack): Boolean {
        return getFlag(stack, CAN_NOT_CLICK)
    }

    @JvmStatic
    fun noSlotHighlight(stack: ItemStack): Boolean {
        return getFlag(stack, NO_SLOT_HIGHLIGHT)
    }

    @JvmStatic
    fun noCrossHair(stack: ItemStack): Boolean {
        return getFlag(stack, NO_CROSS_HAIR)
    }

    private fun getFlag(stack: ItemStack, index: Int): Boolean {
        val nbt = stack.getSubNbt(NAMESPACE) ?: return false
        return nbt.getInt(FLAGS) shr index and 1 == 1
    }

    @JvmStatic
    fun getThirdPersonStack(stack: ItemStack): ItemStack {
        return getStackFromTag(stack, THIRD_PERSON_STACK)
    }

    private fun getGuiStack(stack: ItemStack): ItemStack {
        return getStackFromTag(stack, GUI_STACK)
    }

    private fun getStackFromTag(stack: ItemStack, path: String): ItemStack {
        val nbt = stack.getSubNbt(NAMESPACE) ?: return ItemStack.EMPTY
        val compound = nbt.getCompound(path)
        return ItemStack.fromNbt(compound)
    }

    @JvmStatic
    fun getGunState(stack: ItemStack): GunState? {
        val nbt = stack.getSubNbt(NAMESPACE) ?: return null
        val ordinal = nbt.getInt(GUN_STATE)
        return try {
            GunState.entries[ordinal]
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun getGunModelState(firstPerson: Boolean, stack: ItemStack): Int {
        val nbt = stack.getSubNbt(NAMESPACE) ?: return -1
        val stateTagKey = if (firstPerson && States.isRecoiling) GUN_RECOIL_MODEL_STATE else GUN_MODEL_STATE
        return nbt.getInt(stateTagKey)
    }

    @JvmStatic
    fun getZoomLevel(stack: ItemStack): ZoomLevel? {
        val nbt = stack.getSubNbt(NAMESPACE) ?: return null
        return try {
            ZoomLevel.entries[nbt.getInt(ZOOM_LEVEL) - 1]
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun getEquipDuration(stack: ItemStack): Int {
        val nbt = stack.getSubNbt(NAMESPACE) ?: return 0
        return nbt.getInt(EQUIP_DURATION)
    }

    @JvmStatic
    fun getGunModelScale(modelState: Int): Float {
        return ScaleMapReloadListener.scaleMap.getOrDefault(modelState, 1f)
    }

    @JvmStatic
    fun shouldDisableTooltip(stack: ItemStack): Boolean {
        if (!stack.hasCustomName()) {
            return false
        }
        runCatching {
            val string = stack.getSubNbt(ItemStack.DISPLAY_KEY)?.getString(ItemStack.NAME_KEY) ?: return false
            val text = Text.Serializer.fromJson(string) ?: return false
            if (text == Text.EMPTY) {
                return true
            }
            if (text.content == TextContent.EMPTY && text.siblings.isEmpty()) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun swapGuiStack(stack: ItemStack): ItemStack {
        val signGuiStack = getSignGuiStack(stack)
        if (signGuiStack != null) {
            return signGuiStack
        }

        if (!isOnServer) {
            return stack
        }
        if (stack.isEmpty) {
            return stack
        }
        val guiStack = getGuiStack(stack)
        if (guiStack.isEmpty) {
            return stack
        }
        return guiStack
    }

    private fun getSignGuiStack(stack: ItemStack): ItemStack? {
        if (!stack.isIn(ItemTags.SIGNS) && !stack.isIn(ItemTags.HANGING_SIGNS)) {
            return null
        }
        val nbt = stack.nbt ?: return null
        val signTags = nbt.getCompound("BlockEntityTag") ?: return null
        val texts = mutableListOf<Text>()
        if (signTags.contains("front_text")) {
            SignText.CODEC.parse(NbtOps.INSTANCE, signTags.getCompound("front_text"))
                .result()
                .ifPresent { texts.addAll(it.getMessages(false)) }
        }
        if (signTags.contains("back_text")) {
            SignText.CODEC.parse(NbtOps.INSTANCE, signTags.getCompound("back_text"))
                .result()
                .ifPresent { texts.addAll(it.getMessages(false)) }
        }
        val signObject = SignObjectUtils.parse(texts.map(Text::getString)) ?: return null
        return signObject.itemStack
    }
}
