package dev.uten2c.raincoat.mixin.gui.screen.option;

import dev.uten2c.raincoat.option.RaincoatOptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsOptionsScreen.class)
public abstract class MixinControlsOptionsScreen extends GameOptionsScreen {
    public MixinControlsOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addRaincoatButton(CallbackInfo ci) {
        assert client != null;
        var x = this.width / 2 - 155 + 160;
        var y = this.height / 6 - 12;
        var yOffset = 24 * 2;
        addDrawableChild(new ButtonWidget(x, y + yOffset, 150, 20, Text.translatable("raincoat.options.title"), button -> client.setScreen(new RaincoatOptionsScreen(this, gameOptions))));
    }
}
