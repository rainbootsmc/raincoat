package dev.uten2c.raincoat.mixin.gui.screen.ingame;

import dev.uten2c.raincoat.States;
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

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Unique
    private static final Text RELOAD_TEXT = Text.literal("Reload resources").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    @Unique
    private final AtomicInteger offsetY = new AtomicInteger(0);

    public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void renderReloadButton(CallbackInfo ci) {
        if (!FieldObjectReloadListener.getShouldShowItemTab()) {
            return;
        }
        offsetY.set(4);
        addReloadResourcesButton();
        addToggleDebugShapeButton();
    }

    @Unique
    private void addReloadResourcesButton() {
        final var reloadButton = new PressableTextWidget(
                width - textRenderer.getWidth(RELOAD_TEXT) - 4,
                nextOffsetY(),
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
        addDrawableChild(reloadButton);
    }

    @Unique
    private void addToggleDebugShapeButton() {
        final var toggleText = Text.literal("Debug shape: " + (States.getShowDebugShape() ? "true" : "false")).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
        final var toggleShowDebugShapeButton = new PressableTextWidget(
                width - textRenderer.getWidth(toggleText) - 4,
                nextOffsetY(),
                textRenderer.getWidth(toggleText),
                9,
                toggleText,
                button -> {
                    States.setShowDebugShape(!States.getShowDebugShape());
                    clearAndInit();
                },
                textRenderer
        );
        addDrawableChild(toggleShowDebugShapeButton);
    }

    @Unique
    private int nextOffsetY() {
        return offsetY.getAndUpdate(y -> y + 13);
    }
}
