package dev.uten2c.raincoat.model

import kotlinx.serialization.Serializable

@Serializable
data class GunModel(
    val elements: List<ModelElement>,
    val textures: Map<String, Texture>,
    val removeIndices: Map<Int, List<Int>>,
    val modelTransformations: Map<Int, ModelTransformation>,
)
