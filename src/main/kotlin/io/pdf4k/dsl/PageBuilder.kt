package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.Page
import io.pdf4k.domain.Stationary
import io.pdf4k.domain.StyleAttributes

@PdfDsl
class PageBuilder(val style: StyleAttributes?, private val stationary: List<Stationary>) {
    private val builder = ContentBuilder.ForPage()
    private val blockContent = mutableMapOf<String, Component.Content>()

    fun content(style: StyleAttributes? = null, block: ContentBuilder.ForPage.() -> Unit) {
        if (style == null) {
            builder.block()
        } else {
            builder.style(style) {
                block()
            }
        }
    }

    fun block(ref: String, style: StyleAttributes? = null, block: ContentBuilder.ForBlock.() -> Unit) {
        val builder = ContentBuilder.ForBlock()
        if (style == null) {
            builder.block()
        } else {
            builder.style(style) {
                block()
            }
        }
        blockContent[ref] = builder.build()
    }

    fun build() = Page(stationary, style, builder.build(), blockContent)
}