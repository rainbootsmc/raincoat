package dev.uten2c.raincoat.util

import dev.uten2c.raincoat.resource.ScaleMapReloadListener
import dev.uten2c.raincoat.zoom.ZoomLevel
import net.minecraft.item.ItemStack
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
    private const val ZOOM_LEVEL = "ZoomLevel"

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

    @JvmStatic
    fun getGuiStack(stack: ItemStack): ItemStack {
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
            GunState.values()[ordinal]
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun getGunModelState(stack: ItemStack): Int {
        val nbt = stack.getSubNbt(NAMESPACE) ?: return -1
        return nbt.getInt(GUN_MODEL_STATE)
    }

    @JvmStatic
    fun getZoomLevel(stack: ItemStack): ZoomLevel? {
        val nbt = stack.getSubNbt(NAMESPACE) ?: return null
        return try {
            ZoomLevel.values()[nbt.getInt(ZOOM_LEVEL) - 1]
        } catch (e: Exception) {
            null
        }
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
            val text = Text.Serializer.fromJson(string)
            return text?.content == TextContent.EMPTY
        }
        return false
    }
}
