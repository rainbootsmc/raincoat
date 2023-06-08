package dev.uten2c.raincoat.mixin.gui.hud;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.option.OptionManager;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(DrawContext context, CallbackInfo ci) {
        var player = getCameraPlayer();
        if (player == null) {
            return;
        }
        var stack = player.getMainHandStack();
        if (States.isOnServer() && StackUtils.noCrossHair(stack) && OptionManager.getOptions().isHideCrosshairWhenAds()) {
            ci.cancel();
        }
    }
}
