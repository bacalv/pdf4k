package io.pdf4k.dsl

import io.pdf4k.domain.Component

class ItemBuilder<P : PhraseBuilder<P>> (
    private val phrase: PhraseBuilder<P>,
    private val listBuilder: () -> ListBuilder<P>
): ComponentBuilder<Component.ListItem, ItemBuilder<P>>  {
    override val children = mutableListOf<ComponentBuilder<*, *>>()

    fun list(block: ListBuilder<P>.() -> Unit) {
        children += listBuilder().also { it.block() }
    }

    override fun build() = Component.ListItem(
        phrase.build(),
        children.takeIf { it.isNotEmpty() }?.let { children ->
            Component.ItemList(children.map { it.build() })
        }
    )
}