package dev.uten2c.raincoat.mixin.gui.screen.ingame;

import dev.uten2c.raincoat.resource.FieldObjectReloadListener;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Unique
    private static final Text RELOAD_TEXT = Text.literal("F3+T").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));

    public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void renderReloadButton(CallbackInfo ci) {
        if (!FieldObjectReloadListener.getShouldShowItemTab()) {
            return;
        }
        final var widget = new PressableTextWidget(
                width - textRenderer.getWidth(RELOAD_TEXT) - 4,
                height - 9 - 4,
                textRenderer.getWidth(RELOAD_TEXT),
                9,
                RELOAD_TEXT,
                button -> {
                    assert client != null;
                    final var player = client.player;
                    if (player != null) {
                        player.closeHandledScreen();
                    }
                    client.reloadResources();
                },
                textRenderer
        );
        addDrawableChild(widget);
    }
}
