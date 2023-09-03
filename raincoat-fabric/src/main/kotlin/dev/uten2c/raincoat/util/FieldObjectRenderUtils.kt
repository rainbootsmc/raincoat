package dev.uten2c.raincoat.util

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.RotationAxis

object FieldObjectRenderUtils {
    private const val SIGN_POS_CUBE_SIZE = 0.1

    @JvmStatic
    fun drawSignPosCube(matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, rotation: Float) {
        matrices.push()
        matrices.translate(0.5, 0.5, 0.5)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation))

        val vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines())
        drawBox(
            matrices, vertexConsumer,
            SIGN_POS_CUBE_SIZE / -2, SIGN_POS_CUBE_SIZE / -2, SIGN_POS_CUBE_SIZE / -2,
            SIGN_POS_CUBE_SIZE / 2, SIGN_POS_CUBE_SIZE / 2, SIGN_POS_CUBE_SIZE / 2,
            1f, 0.25f, 0.25f, 0.5f,
        );

        matrices.pop()
    }

    @JvmStatic
    fun drawBox(matrices: MatrixStack, vertexConsumer: VertexConsumer, box: Box, red: Float, green: Float, blue: Float, alpha: Float) {
        drawBox(
            matrices, vertexConsumer,
            box.minX, box.minY, box.minZ,
            box.maxX, box.maxY, box.maxZ,
            red, green, blue, alpha,
        )
    }

    @JvmStatic
    fun drawBox(
        matrices: MatrixStack, vertexConsumer: VertexConsumer,
        minX: Double, minY: Double, minZ: Double,
        maxX: Double, maxY: Double, maxZ: Double,
        red: Float, green: Float, blue: Float, alpha: Float,
    ) {
        drawBox(
            matrices, vertexConsumer,
            minX.toFloat(), minY.toFloat(), minZ.toFloat(),
            maxX.toFloat(), maxY.toFloat(), maxZ.toFloat(),
            red, green, blue, alpha,
        )
    }

    @JvmStatic
    fun drawBox(
        matrices: MatrixStack, vertexConsumer: VertexConsumer,
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float,
        red: Float, green: Float, blue: Float, alpha: Float,
    ) {
        val matrix4f = matrices.peek().positionMatrix
        val matrix3f = matrices.peek().normalMatrix
        vertexConsumer.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
        vertexConsumer.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).normal(matrix3f, -1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).normal(matrix3f, -1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
        vertexConsumer.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
        vertexConsumer.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, -1.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, -1.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, -1.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, -1.0f).next()
        vertexConsumer.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 1.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
        vertexConsumer.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha).normal(matrix3f, 0.0f, 0.0f, 1.0f).next()
    }

    @JvmStatic
    fun drawLine(matrices: MatrixStack, vertexConsumer: VertexConsumer, startX: Float, startY: Float, startZ: Float, endX: Float, endY: Float, endZ: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        val matrix4f = matrices.peek().positionMatrix
        val matrix3f = matrices.peek().normalMatrix
        vertexConsumer.vertex(matrix4f, startX, startY, startZ).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
        vertexConsumer.vertex(matrix4f, endX, endY, endZ).color(red, green, blue, alpha).normal(matrix3f, 1.0f, 0.0f, 0.0f).next()
    }
}
