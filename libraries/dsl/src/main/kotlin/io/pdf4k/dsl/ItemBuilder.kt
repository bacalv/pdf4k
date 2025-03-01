package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.Margin
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.TableAttributes

@PdfDsl
class ItemBuilder<P : PhraseBuilder<P>, T : TableBuilder<P, T>> (
    private val phrase: PhraseBuilder<P>,
    private val listBuilder: () -> ListBuilder<P, T>,
    private val tableBuilder: (TableAttributes, StyleAttributes?) -> T
): ComponentBuilder<Component.ListItem, ItemBuilder<P, T>>  {
    override val children = mutableListOf<ComponentBuilder<*, *>>()
    var table: TableBuilder<P, T>? = null

    fun list(styleAttributes: StyleAttributes? = null, block: ListBuilder<P, T>.() -> Unit): ItemBuilder<P, T> {
        if (styleAttributes != null) {
            children += StyleBuilder(styleAttributes, children = mutableListOf(listBuilder().also { it.block() }))
        } else {
            children += listBuilder().also { it.block() }
        }
        return this
    }

    fun table(columns: Int = 1, style: StyleAttributes? = null, widthPercentage: Float? = null, weights: List<Float>? = null, headerRows: Int = 0, extend: Boolean = false, block: TableBuilder<P, T>.() -> Unit) {
        table = tableBuilder(TableAttributes(columns, widthPercentage, weights, Margin.ZERO, headerRows, extend), style).also { it.block() }
    }

    override fun build() = Component.ListItem(
        phrase.build(),
        children.takeIf { it.isNotEmpty() }?.let { children ->
            Component.ItemList(children.map { it.build() })
        },
        table?.build()
    )
}