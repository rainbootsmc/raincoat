package dev.uten2c.raincoat.mixin.gui.screen.ingame;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {
    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlight(Lnet/minecraft/client/gui/DrawContext;III)V"))
    private boolean disableSlotHighlight(DrawContext context, int x, int y, int z) {
        if (!States.isOnServer()) {
            return true;
        }
        if (focusedSlot == null) {
            return true;
        }
        var stack = focusedSlot.getStack();
        return !StackUtils.noSlotHighlight(stack);
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void disableTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        if (!States.isOnServer()) {
            return;
        }
        if (focusedSlot == null) {
            return;
        }
        var stack = focusedSlot.getStack();
        if (StackUtils.shouldDisableTooltip(stack)) {
            ci.cancel();
        }
    }
}
