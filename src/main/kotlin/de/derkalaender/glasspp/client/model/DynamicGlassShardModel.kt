package de.derkalaender.glasspp.client.model

import com.mojang.blaze3d.matrix.MatrixStack
import de.derkalaender.glasspp.glass.GlassType
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.getAllDirections
import de.derkalaender.glasspp.util.rl
import de.derkalaender.glasspp.util.with
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.TransformationMatrix
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.IModelTransform
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.model.Material
import net.minecraft.client.renderer.model.ModelBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import net.minecraftforge.client.model.BakedModelWrapper
import net.minecraftforge.client.model.SimpleModelTransform
import net.minecraftforge.client.model.data.IModelData
import java.util.Random

class DynamicGlassShardModel(private val bakery: ModelBakery, private val originalModel: IBakedModel) :
    CachedDynamicModel<GlassType, DynamicGlassShardModel.BakedModel>(mutableMapOf()) {

    override fun getKeyForStack(stack: ItemStack): GlassType {
        return GlassShard.getGlassType(stack)
    }

    override fun getModelForStack(stack: ItemStack): BakedModel {
        return BakedModel(bakery, GlassShard.getGlassType(stack), originalModel)
    }

    override fun getQuads(state: BlockState?, side: Direction?, rand: Random): MutableList<BakedQuad> =
        originalModel.getQuads(state, side, rand)

    override fun isBuiltInRenderer() = originalModel.isBuiltInRenderer

    override fun isAmbientOcclusion() = originalModel.isAmbientOcclusion

    override fun func_230044_c_() = originalModel.func_230044_c_()

    override fun isGui3d() = originalModel.isGui3d

    override fun getParticleTexture(): TextureAtlasSprite = originalModel.particleTexture

    override fun doesHandlePerspectives() = true

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return originalModel.itemCameraTransforms
    }

    inner class BakedModel(bakery: ModelBakery, glassType: GlassType, original: IBakedModel) :
        BakedModelWrapper<IBakedModel>(original) {
        private val quads = mutableListOf<BakedQuad>()
        private val faceQuads = mutableMapOf<Direction, MutableList<BakedQuad>>()

        init {
            println("Created new model")

            val transform: IModelTransform = SimpleModelTransform(
                TransformationMatrix.identity()
            )

            val name = rl("glass_shard_with_" + glassType.getResourceLocation().toString().replace(':', '_'))

            val glassUnbaked = bakery.getUnbakedModel(rl("glass_shard_glass") with "inventory")

            val glassSpriteLocation = itemModelMesher.getParticleIcon(glassType.getItem())

            val textureGetterHack = { mat: Material ->
                if (mat.textureLocation == rl("item/glass_shard/test")) {
                    glassSpriteLocation
                } else {
                    defaultTextureGetter(mat)
                }
            }

            val glassBaked = glassUnbaked.bake(bakery, transform, textureGetterHack, name)

            getAllDirections().forEach { faceQuads[it] = mutableListOf() }

            quads.addAll(glassBaked.getQuads())
            quads.addAll(original.getQuads())

            getAllDirections().forEach {
                faceQuads[it]?.addAll(glassBaked.getQuads(it))
                faceQuads[it]?.addAll(original.getQuads(it))
            }
        }

        override fun getQuads(state: BlockState?, face: Direction?, rand: Random): MutableList<BakedQuad> {
            return if (face == null) quads else faceQuads[face]!!
        }

        override fun getQuads(
            state: BlockState?,
            side: Direction?,
            rand: Random,
            extraData: IModelData
        ): MutableList<BakedQuad> {
            return getQuads(state, side, rand)
        }

        override fun handlePerspective(
            cameraTransformType: ItemCameraTransforms.TransformType,
            mat: MatrixStack
        ): IBakedModel {
            return this
        }
    }
}