package dev.uten2c.raincoat.gui

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.PressableTextWidget
import net.minecraft.text.Text
import net.minecraft.util.Util
import kotlin.math.sin

class UpdateButton(x: Int, y: Int, width: Int, height: Int, text: Text, onPress: PressAction, textRenderer: TextRenderer) : PressableTextWidget(x, y, width, height, text, onPress, textRenderer) {
    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val matrices = context.matrices
        matrices.push()
        val time = Util.getMeasuringTimeMs()
        val v = (sin(time / 200.0).toFloat() + 1) * 0.5f
        matrices.translate(0f, v, 0f)
        super.renderButton(context, mouseX, mouseY, delta)
        matrices.pop()
    }
}
