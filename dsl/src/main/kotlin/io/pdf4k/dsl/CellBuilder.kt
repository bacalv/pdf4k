package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.Margin
import java.util.*

class CellBuilder(
    private val colSpan: Int,
    private val rowSpan: Int,
    private val margin: Margin,
    private val childBuilder: ComponentBuilder<*, *>
) : ComponentBuilder<Component.Cell, CellBuilder> {
    override val children: MutableList<ComponentBuilder<*, *>> = EMPTY

    override fun build() =
        when (childBuilder) {
            is PhraseBuilder -> Component.Cell.Text(colSpan, rowSpan, childBuilder.build())
            is TableBuilder<*, *> -> Component.Cell.Table(colSpan, rowSpan, margin, childBuilder.build())
            is ImageBuilder -> Component.Cell.Image(colSpan, rowSpan, childBuilder.build())
            is QrBuilder -> Component.Cell.QrCode(colSpan, rowSpan, childBuilder.build())
            else -> throw IllegalStateException("Incompatible child builder of type ${childBuilder::class.simpleName}")
        }

    companion object {
        val EMPTY: MutableList<ComponentBuilder<*, *>> = Collections.unmodifiableList(emptyList<ComponentBuilder<*, *>>())
    }
}
