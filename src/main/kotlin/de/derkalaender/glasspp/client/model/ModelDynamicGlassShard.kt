package de.derkalaender.glasspp.client.model

import de.derkalaender.glasspp.glass.GlassType
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.rl
import de.derkalaender.glasspp.util.with
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.TransformationMatrix
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.model.ItemLayerModel
import java.util.Random

class ModelDynamicGlassShard(val hasOverrides: Boolean, val base: IBakedModel, internal val glassType: GlassType) :
    IBakedModel {

    companion object {
        val LOCATION = rl("glass_shard") with "inventory"
    }

    override fun getQuads(state: BlockState?, side: Direction?, rand: Random): MutableList<BakedQuad> {
        val quads = mutableListOf<BakedQuad>()

        // val transforms = PerspectiveMapWrapper.getTransforms(base.itemCameraTransforms)
        val glassMaterial = ForgeHooksClient.getBlockMaterial(glassType.getResourceLocation().let {
            ResourceLocation(
                it.namespace,
                "block/" + it.path
            )
        })

        quads.addAll(
            ItemLayerModel.getQuadsForSprite(
                0,
                glassMaterial.sprite,
                TransformationMatrix.identity()
            )
        )

        return quads
    }

    override fun getOverrides(): ItemOverrideList {
        return if (hasOverrides) {
            object : ItemOverrideList() {
                override fun getModelWithOverrides(
                    model: IBakedModel,
                    stack: ItemStack,
                    worldIn: World?,
                    entityIn: LivingEntity?
                ): IBakedModel? {
                    val glassType = GlassShard.getGlassType(stack)

                    return ModelDynamicGlassShard(false, base, glassType)
                }
            }
        } else {
            ItemOverrideList.EMPTY
        }
    }

    override fun isBuiltInRenderer() = base.isBuiltInRenderer

    override fun isAmbientOcclusion() = base.isAmbientOcclusion

    override fun func_230044_c_() = base.func_230044_c_()

    override fun isGui3d() = base.isGui3d

    override fun getParticleTexture(): TextureAtlasSprite = base.particleTexture
}
