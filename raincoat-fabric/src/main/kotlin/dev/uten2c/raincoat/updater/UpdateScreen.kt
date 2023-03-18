package dev.uten2c.raincoat.updater

import kotlinx.coroutines.launch
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class UpdateScreen : Screen(Text.literal("Raincoat Updater")) {
    private var completed = false
    private var error = false

    init {
        Updater.scope.launch {
            if (Updater.startDownload()) {
                completed = true
            } else {
                error = true
            }
        }
    }

    override fun shouldCloseOnEsc(): Boolean {
        return false
    }

    override fun init() {
        if (completed || error) {
            val buttonWidth = 200
            addDrawableChild(
                ButtonWidget.builder(Text.translatable("raincoat.updater.close_game")) {
                    assert(client != null)
                    client!!.scheduleStop()
                }.dimensions(width / 2 - buttonWidth / 2, height / 2 + textRenderer.fontHeight, buttonWidth, 20).build(),
            )
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        drawCenteredTextWithShadow(matrices, textRenderer, title, width / 2, textRenderer.fontHeight, 0xAAAAAA)
        var message: Text? = null
        if (error) {
            message = Text.literal("エラーが発生しました。Minecraftを再起動してください。").setStyle(Style.EMPTY.withColor(Formatting.RED))
        } else if (completed) {
            message = Text.literal("完了しました。Minecraftを再起動してください。")
        }
        if (message != null) {
            drawCenteredTextWithShadow(matrices, textRenderer, message, width / 2, height / 2 - textRenderer.fontHeight, 0xFFFFFF)
        }
        super.render(matrices, mouseX, mouseY, delta)
    }
}
