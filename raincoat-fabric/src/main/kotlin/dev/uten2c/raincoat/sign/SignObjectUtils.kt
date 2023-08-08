package dev.uten2c.raincoat.sign

import net.minecraft.block.*
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationPropertyHelper
import net.minecraft.util.math.Vec3d

object SignObjectUtils {
    @JvmStatic
    fun getYaw(state: BlockState): Float {
        return if (state.contains(WallSignBlock.FACING)) {
            val direction = state.get(WallSignBlock.FACING)
            val rotation = RotationPropertyHelper.fromDirection(direction)
            RotationPropertyHelper.toDegrees(rotation)
        } else {
            RotationPropertyHelper.toDegrees(state.get(SignBlock.ROTATION))
        }
    }

    fun parse(texts: List<String>): SignObject? {
        if (!texts[0].equals("object", true)) {
            return null
        }

        val ids = parseModelIds(texts[1]) ?: return null
        var tableId: String? = null
        var replaceBlock: Block? = null
        var bbStart: Vec3d? = null
        var bbEnd: Vec3d? = null
        var bbDisplay = false
        texts.drop(2)
            .map { it.split("/") }
            .filter { it.size == 2 }
            .map { it[0] to it[1] }
            .forEach { (key, value) ->
                when (key) {
                    "t" -> tableId = value
                    "b" -> replaceBlock = Registries.BLOCK.get(Identifier(value))
                    "bbs" -> bbStart = parseVec3d(value)
                    "bbe" -> bbEnd = parseVec3d(value)
                    "bbd" -> bbDisplay = value.toBooleanStrictOrNull() ?: false
                }
            }
        return SignObject(
            ids,
            replaceBlock ?: Blocks.BARRIER,
            tableId,
            bbStart ?: Vec3d(-0.5, -0.5, -0.5),
            bbEnd ?: Vec3d(0.5, 0.5, 0.5),
            bbDisplay,
        )
    }

    private fun parseModelIds(string: String): List<Int>? {
        val idOrNullList = string.split("/")
            .map { it.toIntOrNull() }

        if (null in idOrNullList) {
            return null
        }

        return idOrNullList.filterNotNull()
    }

    private fun parseVec3d(string: String): Vec3d? {
        val values = string.split(",")
            .map { it.toDoubleOrNull() }
        if (null in values || values.size != 3) {
            return null
        }
        return Vec3d(values[0]!!, values[1]!!, values[2]!!)
    }
}
