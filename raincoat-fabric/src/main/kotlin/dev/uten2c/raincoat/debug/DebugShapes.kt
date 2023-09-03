package dev.uten2c.raincoat.debug

import net.minecraft.util.Identifier
import java.util.concurrent.ConcurrentHashMap

object DebugShapes {
    private val debugShapeMap = ConcurrentHashMap<Identifier, DebugShape>()

    @JvmStatic
    val shapes: Collection<DebugShape>
        get() = debugShapeMap.values.toSet()

    fun addShape(id: Identifier, debugShape: DebugShape) {
        debugShapeMap[id] = debugShape
    }

    fun removeShape(id: Identifier) {
        debugShapeMap.remove(id)
    }

    fun clearShapesWithNamespace(namespace: String) {
        debugShapeMap.keys.removeIf {
            it.namespace == namespace
        }
    }

    fun clearShapes() {
        debugShapeMap.clear()
    }
}
