package dev.uten2c.raincoat.updater

import kotlinx.coroutines.launch
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.math.cos
import kotlin.math.sin

class UpdateScreen : Screen(Text.literal("Raincoat Updater")) {
    private lateinit var closeButton: ButtonWidget
    private var updateStatus: UpdateStatus = UpdateStatus.Updating
        set(value) {
            closeButton.message = if (value is UpdateStatus.ErrorReturnToTitle) toTitleButtonLabel else closeButtonLabel
            field = value
        }
        get() = field

    init {
        Updater.scope.launch {
            updateStatus = Updater.startDownload()
            updateCloseButtonState()
        }
    }

    override fun shouldCloseOnEsc(): Boolean {
        return false
    }

    override fun init() {
        closeButton = ButtonWidget.builder(closeButtonLabel) {
            if (updateStatus is UpdateStatus.ErrorReturnToTitle) {
                client?.setScreen(null)
            } else {
                client?.scheduleStop()
            }
        }.dimensions(width / 2 - BUTTON_WIDTH / 2, height / 2 + textRenderer.fontHeight, BUTTON_WIDTH, 20).build()
        addDrawableChild(closeButton)
        updateCloseButtonState()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, textRenderer.fontHeight, 0xAAAAAA)
        val message = when (val updateStatus = updateStatus) {
            UpdateStatus.Completed -> Text.literal("完了しました。Minecraftを再起動してください。")
            is UpdateStatus.ErrorReturnToTitle -> Text.literal(updateStatus.message).setStyle(Style.EMPTY.withColor(Formatting.RED))
            is UpdateStatus.ErrorShouldRestart -> Text.literal(updateStatus.message).setStyle(Style.EMPTY.withColor(Formatting.RED))
            UpdateStatus.Updating -> Text.literal("Raincoatを更新中")
        }
        context.drawCenteredTextWithShadow(
            textRenderer,
            message,
            width / 2,
            height / 2 - textRenderer.fontHeight - textRenderer.fontHeight,
            0xFFFFFF,
        )
        if (updateStatus is UpdateStatus.Updating) {
            val count = 4
            repeat(count) { i ->
                val rs = sin(System.currentTimeMillis() / 100.0 + i) * -3.5
                val rc = cos(System.currentTimeMillis() / 100.0 + i) * 3.5
                context.matrices.push()
                context.matrices.translate(rs.toFloat(), rc.toFloat(), 0f)
                context.drawCenteredTextWithShadow(
                    textRenderer,
                    "･",
                    width / 2,
                    height / 2 - textRenderer.fontHeight + textRenderer.fontHeight / 2,
                    0x888888,
                )
                context.matrices.pop()
            }
        }
        super.render(context, mouseX, mouseY, delta)
    }

    private fun updateCloseButtonState() {
        closeButton.active = updateStatus != UpdateStatus.Updating
    }

    companion object {
        private const val BUTTON_WIDTH = 200
        private val closeButtonLabel = Text.translatable("raincoat.updater.close_game")
        private val toTitleButtonLabel = Text.translatable("gui.toTitle")
    }
}
