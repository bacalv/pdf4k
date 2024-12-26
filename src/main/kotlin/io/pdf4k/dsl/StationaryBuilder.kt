package io.pdf4k.dsl

import io.pdf4k.domain.Margin
import io.pdf4k.domain.Margin.Companion.DEFAULT_MARGIN
import io.pdf4k.domain.Block
import io.pdf4k.domain.Stationary

@PdfDsl
class StationaryBuilder(private val template: String, private val templatePage: Int, private val margin: Margin) {
    private val blocks = mutableMapOf<String, Block>()
    private var contentFlow: List<String> = emptyList()

    fun block(name: String, x: Float, y: Float, w: Float, h: Float) {
        blocks[name] = Block(x, y, w, h)
    }

    fun contentFlow(vararg name: String) {
        contentFlow += name
    }

    fun build() = Stationary(template, templatePage, margin, blocks, contentFlow)

    companion object {
        fun Stationary.withBlocks(block: StationaryBuilder.() -> Unit): Stationary {
            val builder = StationaryBuilder(template, templatePage, margin)
            builder.block()
            return builder.build()
        }
        fun stationary(template: String, templatePage: Int = 1, margin: Margin = DEFAULT_MARGIN,
                       block: StationaryBuilder.() -> Unit = {}): Stationary {
            val builder = StationaryBuilder(template, templatePage, margin)
            builder.block()
            return builder.build()
        }
    }
}