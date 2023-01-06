package dev.uten2c.raincoat.mixin;

import dev.uten2c.raincoat.option.Options;
import dev.uten2c.raincoat.util.StackUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.SmoothUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
    @Shadow
    private double cursorDeltaX;

    @Shadow
    private double cursorDeltaY;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private SmoothUtil cursorXSmoother;

    @Shadow
    @Final
    private SmoothUtil cursorYSmoother;

    @Inject(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SmoothUtil;clear()V", ordinal = 2), cancellable = true)
    private void zoomMouse(CallbackInfo ci) {
        var player = client.player;
        if (player == null) {
            return;
        }
        var stack = player.getMainHandStack();
        var zoomLevel = StackUtils.getZoomLevel(stack);
        if (zoomLevel == null) {
            return;
        }

        var value = this.client.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
        var baseAmount = value * value * value;
        var amount = baseAmount * 8.0;

        cursorXSmoother.clear();
        cursorYSmoother.clear();
        var x = 0.0;
        var y = 0.0;
        switch (zoomLevel) {
            case ZOOM_1X -> {
                x = cursorDeltaX * amount * Options.getAdsRelativeSensibility();
                y = cursorDeltaY * amount * Options.getAdsRelativeSensibility();
            }
            case ZOOM_2X -> {
                x = cursorDeltaX * amount * Options.getScope2xRelativeSensibility();
                y = cursorDeltaY * amount * Options.getScope2xRelativeSensibility();
            }
            case ZOOM_4X -> {
                x = cursorDeltaX * amount * Options.getScope4xRelativeSensibility();
                y = cursorDeltaY * amount * Options.getScope4xRelativeSensibility();
            }
        }
        cursorDeltaX = 0;
        cursorDeltaY = 0;
        var direction = client.options.getInvertYMouse().getValue() ? -1 : 1;
        player.changeLookDirection(x, y * direction);
        ci.cancel();
    }
}
