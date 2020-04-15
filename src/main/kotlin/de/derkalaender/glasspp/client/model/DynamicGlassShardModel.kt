package de.derkalaender.glasspp.client.model

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Pair
import de.derkalaender.glasspp.MODID
import de.derkalaender.glasspp.glass.GlassType
import de.derkalaender.glasspp.glass.GlassTypes
import de.derkalaender.glasspp.items.GlassShard
import de.derkalaender.glasspp.util.from
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.TransformationMatrix
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.IModelTransform
import net.minecraft.client.renderer.model.IUnbakedModel
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.model.ItemOverrideList
import net.minecraft.client.renderer.model.Material
import net.minecraft.client.renderer.model.ModelBakery
import net.minecraft.client.renderer.texture.MissingTextureSprite
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.resources.IResourceManager
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.model.BakedItemModel
import net.minecraftforge.client.model.IModelConfiguration
import net.minecraftforge.client.model.IModelLoader
import net.minecraftforge.client.model.ItemLayerModel
import net.minecraftforge.client.model.ItemTextureQuadConverter
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelTransformComposition
import net.minecraftforge.client.model.PerspectiveMapWrapper
import net.minecraftforge.client.model.geometry.IModelGeometry
import net.minecraftforge.resource.VanillaResourceType
import java.util.function.Function

/**
 * Adapted from [Forge's DynamicBucketModel][net.minecraftforge.client.model.DynamicBucketModel]
 */
class DynamicGlassShardModel(private val glassType: GlassType, private val dummy: Boolean = false) :
    IModelGeometry<DynamicGlassShardModel> {
    fun withGlassType(glassType: GlassType) = DynamicGlassShardModel(glassType)

    override fun bake(
        owner: IModelConfiguration,
        bakery: ModelBakery,
        spriteGetter: Function<Material, TextureAtlasSprite>,
        modelTransform: IModelTransform,
        overrides: ItemOverrideList,
        modelLocation: ResourceLocation
    ): IBakedModel {
        val missingTextureSprite by lazy { spriteGetter.apply(ForgeHooksClient.getBlockMaterial(MissingTextureSprite.getLocation())) }

        // Transforms
        val transformsFromModel = owner.combinedTransform
        val transformMap =
            PerspectiveMapWrapper.getTransforms(ModelTransformComposition(transformsFromModel, modelTransform))
        val transform = modelTransform.rotation

        // Return early for the first time as we're only interested in the real time item overrides
        // This way ItemModelMesher#getParticleIcon doesn't throw a null pointer exception
        if (dummy) return BakedModel(
            bakery,
            ImmutableList.of(),
            missingTextureSprite,
            Maps.immutableEnumMap(transformMap),
            transform.isIdentity,
            owner.isSideLit,
            this,
            owner,
            modelTransform,
            mutableMapOf()
        )

        // Shard materials
        val particleLocation = owner.resolveTexture("particle")
        val maskLocation = owner.resolveTexture("mask")
        val frameLocation = owner.resolveTexture("frame")

        // Sprites
        val maskSprite = spriteGetter.apply(maskLocation)
        val frameSprite = spriteGetter.apply(frameLocation)

        val glassSprite = Minecraft.getInstance().itemRenderer.itemModelMesher.getParticleIcon(glassType.getItem())

        // If no particle is defined (probable), then just use the underlying glass as the sprite
        val particleSprite =
            if (particleLocation.hasMissingTextureSprite()) glassSprite else spriteGetter.apply(particleLocation)

        val quads = mutableListOf<BakedQuad>()

        // Add frame quads
        quads.addAll(ItemLayerModel.getQuadsForSprite(0, frameSprite, transform))

        // Add quads from underlying glass shaped as shard
        quads.addAll(
            ItemTextureQuadConverter.convertTexture(
                transform,
                maskSprite,
                glassSprite,
                7.498f / 16f,
                Direction.NORTH,
                -0x1,
                1
            )
        )
        quads.addAll(
            ItemTextureQuadConverter.convertTexture(
                transform,
                maskSprite,
                glassSprite,
                8.502f / 16f,
                Direction.SOUTH,
                -0x1,
                1
            )
        )

        return BakedModel(
            bakery,
            ImmutableList.copyOf(quads), // Actual quads that make up the model
            spriteGetter.apply(particleLocation), // Particle sprites
            Maps.immutableEnumMap(transformMap),
            transform.isIdentity,
            owner.isSideLit,
            this,
            owner,
            modelTransform,
            mutableMapOf() // Used for caching
        )
    }

    private fun Material.hasMissingTextureSprite() = MissingTextureSprite.getLocation() == textureLocation

    override fun getTextures(
        owner: IModelConfiguration,
        modelGetter: Function<ResourceLocation, IUnbakedModel>,
        missingTextureErrors: MutableSet<Pair<String, String>>
    ): MutableCollection<Material> {
        return mutableSetOf(
            owner.resolveTexture("particle"),
            owner.resolveTexture("mask"),
            owner.resolveTexture("frame")
        )
    }

    private inner class BakedModel(
        bakery: ModelBakery,
        quads: ImmutableList<BakedQuad>,
        particle: TextureAtlasSprite,
        transforms: ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix>,
        untransformed: Boolean,
        isSideLit: Boolean,

        val parent: DynamicGlassShardModel,
        val owner: IModelConfiguration,
        val originalTransform: IModelTransform,
        val cache: MutableMap<GlassType, IBakedModel>
    ) : BakedItemModel(quads, particle, transforms, GlassTypeOverrideHandler(bakery), untransformed, isSideLit)

    private inner class GlassTypeOverrideHandler(private val bakery: ModelBakery) : ItemOverrideList() {
        override fun getModelWithOverrides(
            originalModel: IBakedModel,
            stack: ItemStack,
            worldIn: World?,
            entityIn: LivingEntity?
        ): IBakedModel? {
            val model = originalModel as BakedModel
            val glassType = GlassShard.getGlassType(stack)

            if (!model.cache.containsKey(glassType)) {
                val parent = model.parent.withGlassType(glassType)

                val bakedModel = parent.bake(
                    model.owner,
                    bakery,
                    ModelLoader.defaultTextureGetter(),
                    model.originalTransform,
                    model.overrides,
                    "glass_shard" from MODID
                )
                model.cache[glassType] = bakedModel
                return bakedModel
            } else {
                return model.cache[glassType]
            }
        }
    }

    object Loader : IModelLoader<DynamicGlassShardModel> {

        override fun getResourceType() = VanillaResourceType.MODELS

        override fun onResourceManagerReload(resourceManager: IResourceManager) {}

        override fun read(
            deserializationContext: JsonDeserializationContext,
            modelContents: JsonObject
        ): DynamicGlassShardModel {
            println("called the read method!")
            return DynamicGlassShardModel(GlassTypes.default, true)
        }
    }
}
