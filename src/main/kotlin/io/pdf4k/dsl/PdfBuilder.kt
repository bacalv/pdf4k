package io.pdf4k.dsl

import io.pdf4k.domain.Page
import io.pdf4k.domain.Pdf
import io.pdf4k.domain.Stationary
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.domain.StyleAttributes

@PdfDsl
class PdfBuilder(val style: StyleAttributes?) {
    private val pages = mutableListOf<Page>()

    fun page(
        style: StyleAttributes? = null,
        stationary: Stationary = BlankA4Portrait,
        vararg continuation: Stationary = arrayOf(),
        block: PageBuilder.() -> Unit = {}
    ) {
        val builder = PageBuilder(style, listOf(stationary) + continuation.toList())
        builder.block()
        pages += builder.build()
    }

    private fun build(): Pdf {
        return Pdf(style, pages)
    }

    companion object {
        fun pdf(style: StyleAttributes? = null, block: PdfBuilder.() -> Unit): Pdf {
            val builder = PdfBuilder(style)
            builder.block()
            return builder.build()
        }
    }
}