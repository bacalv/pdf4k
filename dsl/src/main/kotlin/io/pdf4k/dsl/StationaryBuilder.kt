package io.pdf4k.dsl

import io.pdf4k.domain.Block
import io.pdf4k.domain.Margin
import io.pdf4k.domain.Margin.Companion.DEFAULT_MARGIN
import io.pdf4k.domain.ResourceLocation
import io.pdf4k.domain.Stationary
import io.pdf4k.domain.Stationary.Companion.MainBlockName
import java.net.URI

@PdfDsl
class StationaryBuilder(
    private val template: ResourceLocation,
    private val templatePage: Int,
    private val width: Float,
    private val height: Float,
    private val blocks: MutableMap<String, Block>,
    private var contentFlow: List<String> = emptyList()
) {
    fun block(name: String, x: Float, y: Float, w: Float, h: Float) {
        blocks[name] = Block(x, y, w, h)
    }

    fun contentFlow(vararg name: String) {
        contentFlow += name
    }

    fun build() = Stationary(template, templatePage, width, height, blocks, contentFlow)

    companion object {
        fun Stationary.plusBlocks(block: StationaryBuilder.() -> Unit): Stationary {
            val builder = StationaryBuilder(template, templatePage, width, height, blocks.toMutableMap(), contentFlow)
            builder.block()
            return builder.build()
        }

        fun Stationary.withBlocks(block: StationaryBuilder.() -> Unit): Stationary {
            val builder = StationaryBuilder(template, templatePage, width, height, mutableMapOf(), emptyList())
            builder.block()
            return builder.build()
        }

        fun Stationary.withMargin(margin: Margin) = copy(blocks = blocks + (MainBlockName to margin.toBlock(width, height)))

        fun stationary(
            template: ResourceLocation,
            templatePage: Int = 1,
            width: Float,
            height: Float,
            margin: Margin = DEFAULT_MARGIN,
            block: StationaryBuilder.() -> Unit = {}
        ): Stationary {
            val builder = StationaryBuilder(
                template = template,
                templatePage = templatePage,
                width = width,
                height = height,
                blocks = mutableMapOf(MainBlockName to margin.toBlock(width, height)),
                contentFlow = mutableListOf(MainBlockName)
            )
            builder.block()
            return builder.build()
        }

        fun stationary(
            template: String,
            templatePage: Int = 1,
            width: Float,
            height: Float,
            margin: Margin = DEFAULT_MARGIN,
            block: StationaryBuilder.() -> Unit = {}
        ) = stationary(ResourceLocation.Local(template), templatePage, width, height, margin, block)

        fun stationary(
            template: URI,
            templatePage: Int = 1,
            width: Float,
            height: Float,
            margin: Margin = DEFAULT_MARGIN,
            block: StationaryBuilder.() -> Unit = {}
        ) = stationary(ResourceLocation.Remote.Uri(template), templatePage, width, height, margin, block)

    }
}