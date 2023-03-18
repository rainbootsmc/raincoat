package dev.uten2c.raincoat.model

import net.minecraft.block.BlockState
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import java.util.function.Function

data class GunBakedModel(
    private val particleSprite: Sprite,
    val gunModel: GunModel,
    val textureGetter: Function<SpriteIdentifier, Sprite>,
    val rotationContainer: ModelBakeSettings,
    val modelId: Identifier,
) : BakedModel {
    override fun getQuads(state: BlockState?, face: Direction?, random: Random): List<BakedQuad> {
        return emptyList()
    }

    override fun useAmbientOcclusion(): Boolean {
        return true
    }

    override fun hasDepth(): Boolean {
        return true
    }

    override fun isSideLit(): Boolean {
        return false
    }

    override fun isBuiltin(): Boolean {
        return false
    }

    override fun getParticleSprite(): Sprite {
        return particleSprite
    }

    override fun getTransformation(): ModelTransformation {
        return ModelTransformation.NONE
    }

    override fun getOverrides(): ModelOverrideList {
        return ModelOverrideList.EMPTY
    }
}
