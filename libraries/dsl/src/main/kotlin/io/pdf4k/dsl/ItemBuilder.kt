package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.StyleAttributes

@PdfDsl
class ItemBuilder<P : PhraseBuilder<P>> (
    private val phrase: PhraseBuilder<P>,
    private val listBuilder: () -> ListBuilder<P>
): ComponentBuilder<Component.ListItem, ItemBuilder<P>>  {
    override val children = mutableListOf<ComponentBuilder<*, *>>()

    fun list(styleAttributes: StyleAttributes? = null, block: ListBuilder<P>.() -> Unit) {
        if (styleAttributes != null) {
            children += StyleBuilder(styleAttributes, children = mutableListOf(listBuilder().also { it.block() }))
        } else {
            children += listBuilder().also { it.block() }
        }
    }

    override fun build() = Component.ListItem(
        phrase.build(),
        children.takeIf { it.isNotEmpty() }?.let { children ->
            Component.ItemList(children.map { it.build() })
        }
    )
}