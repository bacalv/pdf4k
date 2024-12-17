package io.pdf4k.dsl

import io.pdf4k.domain.Margin
import io.pdf4k.domain.Margin.Companion.DEFAULT_MARGIN
import io.pdf4k.domain.Rectangle
import io.pdf4k.domain.Stationary

@PdfDsl
class StationaryBuilder(private val template: String, private val templatePage: Int, private val margin: Margin) {
    private val blocks = mutableMapOf<String, Rectangle>()

    fun block(name: String, x: Float, y: Float, w: Float, h: Float) {
        blocks[name] = Rectangle(x, y, w, h)
    }

    fun build() = Stationary(template, templatePage, margin, blocks)

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