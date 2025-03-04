package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.Margin
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.TableAttributes

@PdfDsl
class ItemBuilder<F : PhraseBuilder<F>, T : TableBuilder<F, T>>(
    private val phrase: PhraseBuilder<F>,
    private val tableBuilder: (TableAttributes, StyleAttributes?) -> T
): ComponentBuilder<Component.ListItem, ItemBuilder<F, T>>  {
    override val children = mutableListOf<ComponentBuilder<*, *>>()

    fun list(style: StyleAttributes? = null, block: ListBuilder<F, T>.() -> Unit) = this.also {
        table(1, style) {
            listCell { block() }
        }
    }

    fun table(columns: Int = 1, style: StyleAttributes? = null, widthPercentage: Float? = null, weights: List<Float>? = null, headerRows: Int = 0, extend: Boolean = false, block: TableBuilder<F, T>.() -> Unit) = this.also {
        children += tableBuilder(TableAttributes(columns, widthPercentage, weights, Margin.ZERO, headerRows, extend), style).also { it.block() }
    }

    override fun build() = Component.ListItem(
        phrase.build(),
        children.map { it.build() }
    )
}