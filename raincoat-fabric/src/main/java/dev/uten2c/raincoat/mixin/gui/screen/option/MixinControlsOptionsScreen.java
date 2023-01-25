package dev.uten2c.raincoat.mixin.gui.screen.option;

import dev.uten2c.raincoat.option.RaincoatOptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
        var buttonSize = 20;
        var padding = 4;
        addDrawableChild(
                new TexturedButtonWidget(
                        this.width / 2 - 155 - buttonSize - padding, this.height / 6 - 12, buttonSize, buttonSize, 0, 0, buttonSize,
                        new Identifier("raincoat", "textures/button.png"),
                        buttonSize, buttonSize * 2,
                        button -> client.setScreen(new RaincoatOptionsScreen(this, gameOptions))) {{
                    setTooltip(Tooltip.of(Text.translatable("raincoat.options.title")));
                }}
        );
    }
}
