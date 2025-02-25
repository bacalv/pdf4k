package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.ListStyle
import io.pdf4k.domain.StyleAttributes.Companion.style

@PdfDsl
class ListBuilder<F : PhraseBuilder<F>>(
    val phraseBuilder: () -> F
) : BuildsTextStyle<Component.ItemList, ListBuilder<F>> {
    override val children = mutableListOf<ComponentBuilder<*, *>>()
    override val childBuilder = { ListBuilder(phraseBuilder) }

    fun item(text: String) = item { +text }

    fun item(block: F.() -> Unit) = ItemBuilder(phraseBuilder().also { it.block() }) {
        ListBuilder(phraseBuilder)
    }.also { children += it }

    override fun build() = Component.ItemList(children.map { it.build() })

    companion object {
        val numbered = style(listStyle = ListStyle.Numbered())
        val dashed = style(listStyle = ListStyle.Symbol())
        fun numbered(startAt: Int) = style(listStyle = ListStyle.Numbered(startAt))
    }
}