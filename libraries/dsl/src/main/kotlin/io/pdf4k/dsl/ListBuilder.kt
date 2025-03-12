package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.ListStyle
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.TableAttributes

@PdfDsl
class ListBuilder<F : PhraseBuilder<F>, P : ParagraphBuilder<F, P>, T : TableBuilder<F, P, T, C>, C : ContentBuilder<F, P, T, C>>(
    val phraseBuilder: () -> F,
    private val tableBuilder: (TableAttributes, StyleAttributes?) -> T,
) : BuildsStyle<Component.ItemList, ListBuilder<F, P, T, C>> {
    override val children = mutableListOf<ComponentBuilder<*, *>>()
    override val childBuilder = { ListBuilder(phraseBuilder, tableBuilder) }

    fun item(text: String) = item { +text }

    fun item(block: F.() -> Unit) =
        ItemBuilder(phraseBuilder().also { it.block() }, tableBuilder).also { children += it }

    override fun build() = Component.ItemList(children.map { it.build() })

    companion object {
        val numbered = style(listStyle = ListStyle.Numbered())
        val dashed = style(listStyle = ListStyle.Symbol())
        fun numbered(startAt: Int) = style(listStyle = ListStyle.Numbered(startAt))
    }
}