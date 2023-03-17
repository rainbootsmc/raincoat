package dev.uten2c.raincoat.util;

import dev.uten2c.raincoat.resource.ScaleMapReloadListener;
import dev.uten2c.raincoat.zoom.ZoomLevel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public final class StackUtils {
    private static final int IS_GUN_KEY = 0;
    private static final int CAN_NOT_DROP = 1;
    private static final int CAN_NOT_CLICK = 2;
    private static final int NO_SLOT_HIGHLIGHT = 3;
    private static final int NO_CROSS_HAIR = 4;
    private static final @NotNull String NAMESPACE = "rainboots";
    private static final @NotNull String FLAGS = "Flags";
    private static final @NotNull String THIRD_PERSON_STACK = "ThirdPersonStack";
    private static final @NotNull String GUI_STACK = "GuiStack";
    private static final @NotNull String GUN_STATE = "GunState";
    private static final @NotNull String GUN_MODEL_STATE = "GunModelState";
    private static final @NotNull String ZOOM_LEVEL = "ZoomLevel";
    private static final @NotNull String RENDERING_PARTS = "RenderingParts";

    private StackUtils() {
    }

    public static boolean isGun(@NotNull ItemStack stack) {
        return getFlag(stack, IS_GUN_KEY);
    }

    public static boolean canNotDrop(@NotNull ItemStack stack) {
        return getFlag(stack, CAN_NOT_DROP);
    }

    public static boolean canNotClick(@NotNull ItemStack stack) {
        return getFlag(stack, CAN_NOT_CLICK);
    }

    public static boolean noSlotHighlight(@NotNull ItemStack stack) {
        return getFlag(stack, NO_SLOT_HIGHLIGHT);
    }

    public static boolean noCrossHair(@NotNull ItemStack stack) {
        return getFlag(stack, NO_CROSS_HAIR);
    }

    private static boolean getFlag(@NotNull ItemStack stack, int index) {
        var nbt = stack.getSubNbt(NAMESPACE);
        if (nbt == null) {
            return false;
        }
        return (nbt.getInt(FLAGS) >> index & 1) == 1;
    }

    public static @NotNull ItemStack getThirdPersonStack(@NotNull ItemStack stack) {
        return getStackFromTag(stack, THIRD_PERSON_STACK);
    }

    public static @NotNull ItemStack getGuiStack(@NotNull ItemStack stack) {
        return getStackFromTag(stack, GUI_STACK);
    }

    private static @NotNull ItemStack getStackFromTag(@NotNull ItemStack stack, @NotNull String path) {
        var nbt = stack.getSubNbt(NAMESPACE);
        if (nbt == null) {
            return ItemStack.EMPTY;
        }
        var compound = nbt.getCompound(path);
        return ItemStack.fromNbt(compound);
    }

    public static @Nullable GunState getGunState(@NotNull ItemStack stack) {
        var nbt = stack.getSubNbt(NAMESPACE);
        if (nbt == null) {
            return null;
        }
        var ordinal = nbt.getInt(GUN_STATE);
        try {
            return GunState.values()[ordinal];
        } catch (Exception e) {
            return null;
        }
    }

    public static int getGunModelState(@NotNull ItemStack stack) {
        var nbt = stack.getSubNbt(NAMESPACE);
        if (nbt == null) {
            return -1;
        }
        return nbt.getInt(GUN_MODEL_STATE);
    }


    public static @Nullable ZoomLevel getZoomLevel(@NotNull ItemStack stack) {
        var nbt = stack.getSubNbt(NAMESPACE);
        if (nbt == null) {
            return null;
        }
        try {
            return ZoomLevel.values()[nbt.getInt(ZOOM_LEVEL) - 1];
        } catch (Exception e) {
            return null;
        }
    }

    public static float getGunModelScale(int modelState) {
        return ScaleMapReloadListener.getScaleMap().getOrDefault("" + modelState, 1.0).floatValue();
    }

    public static Collection<String> getRenderingParts(@NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return Collections.emptyList();
        }
        final var nbt = stack.getSubNbt(NAMESPACE);
        if (nbt == null) {
            return Collections.emptyList();
        }
        return nbt.getList(RENDERING_PARTS, NbtElement.STRING_TYPE).stream()
                .map(NbtElement::asString)
                .toList();
    }
}
