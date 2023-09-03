package dev.uten2c.raincoat.sign

import dev.uten2c.raincoat.model.Display
import net.minecraft.util.math.Box

data class FieldObjectModelMetadata(val display: Display, val collision: Collection<Box>, val interaction: Collection<Box>, val removeIndices: Collection<Int>) {
    companion object {
        private val metadataMap = mutableMapOf<String, FieldObjectModelMetadata>()
        private val metadataIdMap = mutableMapOf<Int, FieldObjectModelMetadata>()

        fun clear() {
            metadataMap.clear()
            metadataIdMap.clear()
        }

        fun put(key: String, metadata: FieldObjectModelMetadata) {
            metadataMap[key] = metadata
        }

        fun put(model: Int, metadata: FieldObjectModelMetadata) {
            metadataIdMap[model] = metadata
        }

        fun getOrNull(key: String): FieldObjectModelMetadata? {
            return metadataMap[key]
        }

        @JvmStatic
        fun getOrNull(model: Int): FieldObjectModelMetadata? {
            return metadataIdMap[model]
        }

        @JvmStatic
        fun getOrDefault(key: String): FieldObjectModelMetadata {
            return metadataMap[key] ?: FieldObjectModelMetadata(Display.IDENTITY, emptySet(), emptySet(), emptySet())
        }
    }
}
