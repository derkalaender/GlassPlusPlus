package de.derkalaender.glasspp.client.model

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemModelMesher
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.BlockModel
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.IModelTransform
import net.minecraft.client.renderer.model.IUnbakedModel
import net.minecraft.client.renderer.model.ItemModelGenerator
import net.minecraft.client.renderer.model.Material
import net.minecraft.client.renderer.model.ModelBakery
import net.minecraft.client.renderer.model.ModelManager
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.data.EmptyModelData
import java.util.Random
import java.util.function.Function

val itemModelMesher: ItemModelMesher by lazy { Minecraft.getInstance().itemRenderer.itemModelMesher }
val modelManager: ModelManager by lazy { Minecraft.getInstance().modelManager }

val defaultTextureGetter = { mat: Material -> ModelLoader.defaultTextureGetter().apply(mat) }

fun IUnbakedModel.bake(
    bakery: ModelBakery,
    transform: IModelTransform,
    textureGetter: (Material) -> TextureAtlasSprite,
    name: ResourceLocation
): IBakedModel {
    return if (this is BlockModel && this.rootModel == ModelBakery.MODEL_GENERATED) {
        ItemModelGenerator().makeItemModel(Function(textureGetter), this)
            .bakeModel(bakery, this, ModelLoader.defaultTextureGetter(), transform, name, false)
    } else {
        bakeModel(bakery, Function(textureGetter), transform, name)!!
    }
}

fun IBakedModel.isMissing() = this == modelManager.missingModel

fun IBakedModel.getQuads(side: Direction? = null): List<BakedQuad> =
    getQuads(null, side, Random(), EmptyModelData.INSTANCE)
