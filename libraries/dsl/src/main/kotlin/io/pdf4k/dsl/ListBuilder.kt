package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.ListStyle
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.TableAttributes

@PdfDsl
class ListBuilder<F : PhraseBuilder<F>, T : TableBuilder<F, T>>(
    val phraseBuilder: () -> F,
    val tableBuilder: (TableAttributes, StyleAttributes?) -> T
) : BuildsTextStyle<Component.ItemList, ListBuilder<F, T>> {
    override val children = mutableListOf<ComponentBuilder<*, *>>()
    override val childBuilder = { ListBuilder(phraseBuilder, tableBuilder) }

    fun item(text: String) = item { +text }

    fun item(block: F.() -> Unit) = ItemBuilder(phraseBuilder().also { it.block() }, childBuilder, tableBuilder).also { children += it }

    override fun build() = Component.ItemList(children.map { it.build() })

    companion object {
        val numbered = style(listStyle = ListStyle.Numbered())
        val dashed = style(listStyle = ListStyle.Symbol())
        fun numbered(startAt: Int) = style(listStyle = ListStyle.Numbered(startAt))
    }
}