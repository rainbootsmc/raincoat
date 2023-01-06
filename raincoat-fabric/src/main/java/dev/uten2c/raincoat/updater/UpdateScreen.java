package dev.uten2c.raincoat.updater;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class UpdateScreen extends Screen {
    private boolean completed = false;
    private boolean error = false;

    public UpdateScreen() {
        super(Text.literal("Raincoat Updater"));
        Updater.tryStartDownload(() -> completed = true, () -> error = true);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (completed || error) {
            var buttonWidth = 200;
            addDrawableChild(new ButtonWidget(width / 2 - buttonWidth / 2, height / 2 + textRenderer.fontHeight, buttonWidth, 20, Text.literal("Minecraftを閉じる"), button -> {
                assert this.client != null;
                this.client.scheduleStop();
            }));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, title, width / 2, textRenderer.fontHeight, 0xAAAAAA);

        Text message = null;
        if (error) {
            message = Text.literal("エラーが発生しました。Minecraftを再起動してください。").setStyle(Style.EMPTY.withColor(Formatting.RED));
        } else if (completed) {
            message = Text.literal("完了しました。Minecraftを再起動してください。");
        }
        if (message != null) {
            drawCenteredText(matrices, textRenderer, message, width / 2, height / 2 - textRenderer.fontHeight, 0xFFFFFF);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }
}
