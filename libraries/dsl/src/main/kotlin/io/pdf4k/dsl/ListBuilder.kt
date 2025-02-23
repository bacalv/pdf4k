package io.pdf4k.dsl

import io.pdf4k.domain.Component

class ListBuilder<P : PhraseBuilder<P>>(
    val phraseBuilder: () -> P
) : BuildsTextStyle<Component.ItemList, ListBuilder<P>> {
    override val children = mutableListOf<ComponentBuilder<*, *>>()
    override val childBuilder = { ListBuilder(phraseBuilder) }

    fun item(text: String) = item { +text }

    fun item(block: P.() -> Unit) = ItemBuilder(phraseBuilder().also { it.block() }) {
        ListBuilder(phraseBuilder)
    }.also { children += it }

    override fun build() = Component.ItemList(
        children.map { it.build() }
    )
}