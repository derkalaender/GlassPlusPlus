// package de.derkalaender.glasspp.client.model
//
// import com.google.common.collect.ImmutableList
// import com.google.common.collect.ImmutableMap
// import com.google.common.collect.Maps
// import com.google.gson.JsonDeserializationContext
// import com.google.gson.JsonObject
// import com.mojang.datafixers.util.Pair
// import de.derkalaender.glasspp.MODID
// import de.derkalaender.glasspp.glass.GlassTypes
// import de.derkalaender.glasspp.items.GlassShard
// import de.derkalaender.glasspp.util.from
// import net.minecraft.client.renderer.TransformationMatrix
// import net.minecraft.client.renderer.model.BakedQuad
// import net.minecraft.client.renderer.model.IBakedModel
// import net.minecraft.client.renderer.model.IModelTransform
// import net.minecraft.client.renderer.model.IUnbakedModel
// import net.minecraft.client.renderer.model.ItemCameraTransforms
// import net.minecraft.client.renderer.model.ItemOverrideList
// import net.minecraft.client.renderer.model.Material
// import net.minecraft.client.renderer.model.ModelBakery
// import net.minecraft.client.renderer.model.ModelResourceLocation
// import net.minecraft.client.renderer.texture.TextureAtlasSprite
// import net.minecraft.entity.LivingEntity
// import net.minecraft.item.Item
// import net.minecraft.item.ItemStack
// import net.minecraft.resources.IResourceManager
// import net.minecraft.util.ResourceLocation
// import net.minecraft.world.World
// import net.minecraftforge.client.ForgeHooksClient
// import net.minecraftforge.client.model.BakedItemModel
// import net.minecraftforge.client.model.IModelConfiguration
// import net.minecraftforge.client.model.IModelLoader
// import net.minecraftforge.client.model.ItemLayerModel
// import net.minecraftforge.client.model.ModelLoader
// import net.minecraftforge.client.model.ModelTransformComposition
// import net.minecraftforge.client.model.PerspectiveMapWrapper
// import net.minecraftforge.client.model.geometry.IModelGeometry
// import net.minecraftforge.resource.VanillaResourceType
// import java.util.function.Function
//
// class DynamicGlassShardModelOld(private val glassItem: Item) : IModelGeometry<DynamicGlassShardModelOld> {
//     private val LOCATION = ModelResourceLocation("glass_shard" from MODID, "inventory")
//
//     fun withGlassItem(glassItem: Item) = DynamicGlassShardModelOld(glassItem)
//
//     override fun bake(
//         owner: IModelConfiguration,
//         bakery: ModelBakery,
//         spriteGetter: Function<Material, TextureAtlasSprite>,
//         modelTransform: IModelTransform,
//         overrides: ItemOverrideList,
//         modelLocation: ResourceLocation
//     ): IBakedModel {
//         // TODO Texture stuff
//         // val particleLocation = owner.resolveTexture("particle") // TODO null check
//         // val coverLocation = owner.resolveTexture("cover")
//
//         val transformsFromModel = owner.combinedTransform
//
//         val transformMap =
//             PerspectiveMapWrapper.getTransforms(ModelTransformComposition(transformsFromModel, modelTransform))
//
//         // var particleSprite = spriteGetter.apply(particleLocation) // TODO maybe null
//
//         val transform = modelTransform.rotation
//
//         // Get the texture of the glass block
//         // val glassSprite = spriteGetter.apply(ForgeHooksClient.getBlockMaterial(glassItem.registryName!!))
//
//         // particleSprite = glassSprite // TODO check if particleSprite is actually null
//
//         val containedItemMaterial = ForgeHooksClient.getBlockMaterial(glassItem.registryName)
//         val containedItemSprite = spriteGetter.apply(containedItemMaterial)
//
//         println("ContainedItemSprite: $containedItemMaterial")
//
//         val quads = mutableListOf<BakedQuad>() // TODO quad stuff
//
//         quads.addAll(ItemLayerModel.getQuadsForSprites(mutableListOf(containedItemMaterial), transform, spriteGetter))
//
//         // val templateSprite = spriteGetter.apply(coverLocation)
//         // quads.addAll(
//         //     ItemTextureQuadConverter.convertTexture(
//         //         transform,
//         //         templateSprite,
//         //         glassSprite,
//         //         7.498f / 16f,
//         //         Direction.NORTH,
//         //         -0x1,
//         //         1
//         //     )
//         // )
//         // quads.addAll(
//         //     ItemTextureQuadConverter.convertTexture(
//         //         transform,
//         //         templateSprite,
//         //         glassSprite,
//         //         8.502f / 16f,
//         //         Direction.SOUTH,
//         //         -0x1,
//         //         1
//         //     )
//         // )
//
//         return BakedModel(
//             bakery,
//             quads as ImmutableList<BakedQuad>,
//             containedItemSprite,
//             Maps.immutableEnumMap(transformMap),
//             transform.isIdentity,
//             owner.isSideLit,
//             this,
//             owner,
//             modelTransform,
//             mutableMapOf()
//         )
//     }
//
//     override fun getTextures(
//         owner: IModelConfiguration,
//         modelGetter: Function<ResourceLocation, IUnbakedModel>?,
//         missingTextureErrors: MutableSet<Pair<String, String>>?
//     ): MutableCollection<Material> {
//         return mutableSetOf(
//
//         )
//     }
//
//     object Loader : IModelLoader<DynamicGlassShardModelOld> {
//
//         override fun getResourceType() = VanillaResourceType.MODELS
//
//         override fun onResourceManagerReload(resourceManager: IResourceManager) {
//         }
//
//         override fun read(
//             deserializationContext: JsonDeserializationContext,
//             modelContents: JsonObject
//         ): DynamicGlassShardModelOld {
//             println("called the read method!")
//             return DynamicGlassShardModelOld(GlassTypes.default.getItem())
//         }
//     }
//
//     private inner class BakedModel(
//         bakery: ModelBakery,
//
//         quads: ImmutableList<BakedQuad>,
//         particle: TextureAtlasSprite,
//         transforms: ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix>,
//         untransformed: Boolean,
//         isSideLit: Boolean,
//
//         val parent: DynamicGlassShardModelOld,
//         val owner: IModelConfiguration,
//         val originalTransform: IModelTransform,
//         val cache: MutableMap<ResourceLocation, IBakedModel>
//     ) : BakedItemModel(quads, particle, transforms, GlassTypeOverrideHandler(bakery), untransformed, isSideLit)
//
//     private inner class GlassTypeOverrideHandler(private val bakery: ModelBakery) : ItemOverrideList() {
//         override fun getModelWithOverrides(
//             originalModel: IBakedModel,
//             stack: ItemStack,
//             worldIn: World?,
//             entityIn: LivingEntity?
//         ): IBakedModel? {
// //            return null
//             val model = originalModel as BakedModel
//             val glassShard = stack.item as GlassShard
//             val containedItem = glassShard.getGlassType(stack) ?: return null
//             val containedItemRegistryName = containedItem.registryName
//
//             println("hallo")
//
//             if (!model.cache.containsKey(containedItemRegistryName)) {
//                 val parent = model.parent.withGlassItem(containedItem)
//
//                 val bakedModel = parent.bake(
//                     model.owner,
//                     bakery,
//                     ModelLoader.defaultTextureGetter(),
//                     model.originalTransform,
//                     model.overrides,
//                     "glass_shard" from MODID
//                 )
//                 model.cache[containedItemRegistryName!!] = bakedModel
//                 return bakedModel
//             } else {
//                 return model.cache[containedItemRegistryName]
//             }
//         }
//     }
// }
