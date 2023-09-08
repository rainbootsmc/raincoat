package dev.uten2c.raincoat.mixin;

import dev.uten2c.raincoat.States;
import dev.uten2c.raincoat.option.OptionManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.NarratorManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Redirect(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/NarratorManager;isActive()Z"))
    private boolean disableNarratorToggleKey(NarratorManager instance) {
        final var narratorDisabled = States.isOnServer() && OptionManager.getOptions().isNarratorDisabled();
        return instance.isActive() && !narratorDisabled;
    }
}
