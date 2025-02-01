package io.pdf4k.domain

import io.pdf4k.domain.Margin.Companion.DEFAULT_MARGIN
import io.pdf4k.domain.ResourceLocation.Companion.local

data class Stationary(
    val template: ResourceLocation,
    val templatePage: Int = 1,
    val width: Float,
    val height: Float,
    val blocks: Map<String, Block> = emptyMap(),
    val contentFlow: List<String> = emptyList()
) {
    constructor(template: ResourceLocation, templatePage: Int, width: Float, height: Float, margin: Margin) : this(
        template = template,
        templatePage = templatePage,
        width = width,
        height = height,
        blocks = mapOf(MainBlockName to margin.toBlock(width, height)),
        contentFlow = listOf(MainBlockName)
    )

    companion object {
        val MainBlockName = "main"
        val BlankA4Landscape = Stationary(local("blank-a4-landscape"), 1, 842.88f, 595.92f, DEFAULT_MARGIN)
        val BlankA4Portrait = Stationary(local("blank-a4-portrait"), 1, 595.92f, 842.88f, DEFAULT_MARGIN)
    }
}
