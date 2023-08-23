package dev.uten2c.raincoat.sign

import dev.uten2c.raincoat.resource.FieldObjectReloadListener
import net.minecraft.block.*
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
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

        val parsedIds = parseModelIds(texts[1])
        var replaceBlock: Block? = null
        var bbStart: Vec3d? = null
        var bbEnd: Vec3d? = null
        var bbDisplay = false
        var offset: Vec3d? = null
        var door: Pair<BlockPos, BlockPos>? = null
        texts.drop(2)
            .map { it.split("/") }
            .filter { it.size == 2 }
            .map { it[0] to it[1] }
            .forEach { (key, value) ->
                when (key) {
                    "b", "block" -> replaceBlock = Registries.BLOCK.get(Identifier(value))
                    "bbs" -> bbStart = parseVec3d(value)
                    "bbe" -> bbEnd = parseVec3d(value)
                    "bb" -> {
                        val result = parseBox(value)
                        if (result != null) {
                            bbStart = result.first
                            bbEnd = result.second
                        }
                    }

                    "bbd" -> bbDisplay = value.toBooleanStrictOrNull() ?: false
                    "o", "offset" -> offset = parseVec3d(value)
                    "door" -> door = parseBlockBox(value)
                }
            }
        return SignObject(
            parsedIds.ids,
            replaceBlock ?: Blocks.BARRIER,
            bbStart ?: Vec3d(-0.5, -0.5, -0.5),
            bbEnd ?: Vec3d(0.5, 0.5, 0.5),
            bbDisplay,
            offset ?: Vec3d(0.0, 0.0, 0.0),
            door,
            parsedIds.isOldId,
        )
    }

    private fun parseModelIds(string: String): ParsedIds {
        var hasOldId = false
        val ids = string.split("/")
            .map {
                if (it.toIntOrNull() != null) {
                    hasOldId = true
                }
                FieldObjectReloadListener.idMap[it] ?: 1000
            }
        return ParsedIds(ids, hasOldId)
    }

    private fun parseVec3d(string: String): Vec3d? {
        val values = string.split(",")
            .map { it.toDoubleOrNull() }
        if (null in values || values.size != 3) {
            return null
        }
        return Vec3d(values[0]!!, values[1]!!, values[2]!!)
    }

    private fun parseBlockPos(string: String): BlockPos? {
        val values = string.split(",")
            .map { it.toIntOrNull() }
        if (null in values || values.size != 3) {
            return null
        }
        return BlockPos(values[0]!!, values[1]!!, values[2]!!)
    }

    private fun parseBox(string: String): Pair<Vec3d, Vec3d>? {
        val values = string.split("->").map(::parseVec3d)
        if (null in values || values.size != 2) {
            return null
        }
        return values[0]!! to values[1]!!
    }

    private fun parseBlockBox(string: String): Pair<BlockPos, BlockPos>? {
        val values = string.split("->").map(::parseBlockPos)
        if (null in values || values.size != 2) {
            return null
        }
        return values[0]!! to values[1]!!
    }

    private data class ParsedIds(val ids: List<Int>, val isOldId: Boolean)
}
