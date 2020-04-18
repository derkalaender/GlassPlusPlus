package de.derkalaender.glasspp.client.model

import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder
import net.minecraftforge.client.model.pipeline.LightUtil
import net.minecraftforge.client.model.pipeline.VertexTransformer
import java.awt.Color

class QuadColorTransformer(original: BakedQuad, color: Color) : VertexTransformer(BakedQuadBuilder()) {
    private val colorArray: FloatArray = color.getRGBComponents(null)

    init {
        LightUtil.putBakedQuad(this, original)
    }

    override fun put(element: Int, vararg data: Float) {
        println("put called")
        if (vertexFormat.elements[element].usage == VertexFormatElement.Usage.COLOR) {
            println("this is color")
            super.put(element, *colorArray)
        } else {
            super.put(element, *data)
        }
    }

    fun build(): BakedQuad = (super.parent as BakedQuadBuilder).build()
}