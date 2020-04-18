package de.derkalaender.glasspp.client.model

import com.mojang.blaze3d.matrix.MatrixStack
import de.derkalaender.glasspp.glass.GlassType
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.average
import de.derkalaender.glasspp.util.getAllDirections
import de.derkalaender.glasspp.util.getAllPixels
import de.derkalaender.glasspp.util.rl
import de.derkalaender.glasspp.util.toBGRA
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
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.model.BakedModelWrapper
import net.minecraftforge.client.model.SimpleModelTransform
import net.minecraftforge.client.model.data.IModelData
import java.util.Random

class DynamicGlassShardModel(private val bakery: ModelBakery, private val frameModel: IBakedModel) :
    CachedDynamicModel<GlassType, DynamicGlassShardModel.BakedModel>(mutableMapOf()) {

    override fun getKeyForStack(stack: ItemStack): GlassType {
        return GlassShard.getGlassType(stack)
    }

    override fun getModelForStack(stack: ItemStack): BakedModel {
        return BakedModel(bakery, GlassShard.getGlassType(stack), this, frameModel)
    }

    // Everything below this delegates to the original model
    override fun getQuads(state: BlockState?, side: Direction?, rand: Random): MutableList<BakedQuad> =
        frameModel.getQuads(state, side, rand)

    override fun isBuiltInRenderer() = frameModel.isBuiltInRenderer

    override fun isAmbientOcclusion() = frameModel.isAmbientOcclusion

    override fun func_230044_c_() = frameModel.func_230044_c_()

    override fun isGui3d() = frameModel.isGui3d

    override fun getParticleTexture(): TextureAtlasSprite = frameModel.particleTexture

    override fun doesHandlePerspectives() = true

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return frameModel.itemCameraTransforms
    }

    // The actual model that's used in runtime
    inner class BakedModel(bakery: ModelBakery, glassType: GlassType, parentModel: IBakedModel, frameModel: IBakedModel) :
        BakedModelWrapper<IBakedModel>(parentModel) {
        private val quads = mutableListOf<BakedQuad>()
        // Needs to account for "directional quads" because some models only return data when a direction is specified
        private val faceQuads = mutableMapOf<Direction, MutableList<BakedQuad>>()

        init {
            // TODO figure out if this is the correct transformation
            val transform: IModelTransform = SimpleModelTransform(TransformationMatrix.identity())

            // Dynamic placeholder name in case it's needed
            val name = rl("glass_shard_with_" + glassType.getResourceLocation().toString().replace(':', '_'))

            val glassSpriteLocation = itemModelMesher.getParticleIcon(glassType.getItem())

            // This is quite hacky as we're just swapping the texture
            // Probably a better way to do this
            val textureGetterHack = { mat: Material ->
                if (mat.textureLocation == rl("item/glass_shard/temp")) {
                    glassSpriteLocation
                } else {
                    defaultTextureGetter(mat)
                }
            }

            val innerModelUnbaked = bakery.getUnbakedModel(rl("glass_shard/inner") with "inventory")

            val innerModel = innerModelUnbaked.bake(bakery, transform, textureGetterHack, name)

            val glassColor = glassSpriteLocation.getAllPixels().average().toBGRA()

            getAllDirections().forEach { faceQuads[it] = mutableListOf() }

            quads.addAll(innerModel.getQuads())
            quads.addAll(frameModel.getQuads().map { quad -> QuadColorTransformer(quad, glassColor).build() })

            // ItemTextureQuadConverter.genQuad()

            getAllDirections().forEach {
                faceQuads[it]?.addAll(innerModel.getQuads(it))
                faceQuads[it]?.addAll(frameModel.getQuads(it).map { quad -> QuadColorTransformer(quad, glassColor).build() })
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
            return ForgeHooksClient.handlePerspective(
                this,
                cameraTransformType,
                mat
            )
        }
    }
}