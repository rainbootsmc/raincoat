package dev.uten2c.raincoat.debug

import dev.uten2c.raincoat.util.FieldObjectRenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Quaternionf

sealed class DebugShape(color: UInt) {
    val alpha = (color shr 24 and 0xFFu).toInt() / 255f
    val red = (color shr 16 and 0xFFu).toInt() / 255f
    val green = (color shr 8 and 0xFFu).toInt() / 255f
    val blue = (color and 0xFFu).toInt() / 255f

    abstract fun draw(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider.Immediate, cameraX: Double, cameraY: Double, cameraZ: Double)

    class Box(private val min: Vec3d, private val max: Vec3d, color: UInt) : DebugShape(color) {
        override fun draw(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider.Immediate, cameraX: Double, cameraY: Double, cameraZ: Double) {
            matrices.push()
            matrices.translate(-cameraX, -cameraY, -cameraZ)
            FieldObjectRenderUtils.drawBox(
                matrices, vertexConsumers.getBuffer(RenderLayer.getLines()),
                min.x, min.y, min.z,
                max.x, max.y, max.z,
                red, green, blue, alpha,
            )
            matrices.pop()
        }
    }

    class RotateBox(private val origin: Vec3d, size: Vec3d, private val rotation: Quaternionf, color: UInt) : DebugShape(color) {
        private val halfX = size.x / 2
        private val halfY = size.y / 2
        private val halfZ = size.z / 2

        override fun draw(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider.Immediate, cameraX: Double, cameraY: Double, cameraZ: Double) {
            matrices.push()
            matrices.translate(-cameraX, -cameraY, -cameraZ)
            matrices.translate(origin.x, origin.y, origin.z)
            matrices.multiply(rotation)
            FieldObjectRenderUtils.drawBox(
                matrices, vertexConsumers.getBuffer(RenderLayer.getLines()),
                -halfX, -halfY, -halfZ,
                halfX, halfY, halfZ,
                red, green, blue, alpha,
            )
            matrices.pop()
        }
    }

    class Message(private val pos: Vec3d, private val message: Text, private val scale: Float, private val seeThrough: Boolean) : DebugShape(0xffffffffu) {
        override fun draw(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider.Immediate, cameraX: Double, cameraY: Double, cameraZ: Double) {
            val client = MinecraftClient.getInstance()
            val textRenderer = client.textRenderer
            matrices.push()
            matrices.translate(pos.x - cameraX, pos.y - cameraY, pos.z - cameraZ)
            matrices.multiplyPositionMatrix(Matrix4f().rotate(client.gameRenderer.camera.rotation))
            matrices.scale(-scale, -scale, -scale)
            val offsetX = textRenderer.getWidth(message) / -2f
            val color = (0xffffffffu).toInt()
            val layer = if (seeThrough) TextRenderer.TextLayerType.SEE_THROUGH else TextRenderer.TextLayerType.NORMAL
            textRenderer.draw(message, offsetX, 0f, color, false, matrices.peek().positionMatrix, vertexConsumers, layer, 0, 0xF000F0)
            matrices.pop()
        }
    }
}
