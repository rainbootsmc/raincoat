package dev.uten2c.raincoat.debug

import dev.uten2c.raincoat.util.FieldObjectRenderUtils
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
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
}
