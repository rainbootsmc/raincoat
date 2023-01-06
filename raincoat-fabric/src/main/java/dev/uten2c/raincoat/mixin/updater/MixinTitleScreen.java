package dev.uten2c.raincoat.mixin.updater;

import dev.uten2c.raincoat.updater.UpdateScreen;
import dev.uten2c.raincoat.updater.Updater;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
    private static final @NotNull Text CLIENT_UPDATE_TEXT = Text.literal("クリックしてRaincoatをアップデート（ベータ）");

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setConnectedToRealms(Z)V"))
    private void init(CallbackInfo ci) {
        if (!Updater.isUpdateAvailable()) {
            Updater.asyncFetchReleaseVersion();
            return;
        }
        var textWidth = this.textRenderer.getWidth(CLIENT_UPDATE_TEXT);
        var x = this.width / 2 - textWidth / 2;
        this.addDrawableChild(new PressableTextWidget(x, 0, textWidth, 10, CLIENT_UPDATE_TEXT, button -> {
            assert client != null;
            client.setScreen(new UpdateScreen());
        }, this.textRenderer));
    }
}
