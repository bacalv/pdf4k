package io.pdf4k.dsl

import io.pdf4k.domain.*

@PdfDsl
abstract class TableBuilder<F : PhraseBuilder<F>, T : TableBuilder<F, T>>(
    private val attributes: TableAttributes,
    private val style: StyleAttributes?
) : BuildsCellStyle<Component.Table, T> {
    override val children: MutableList<ComponentBuilder<*, *>> = mutableListOf()
    abstract val phraseBuilder: () -> F
    abstract val tableBuilder: (TableAttributes, StyleAttributes?) -> T

    override fun build() = with(attributes) {
        if (style == null) {
            Component.Table(columns, widthPercentage, weights, headerRows, children.map { it.build() })
        } else {
            Component.Table(columns, widthPercentage, weights, headerRows, listOf(Component.Style(style, children.map { it.build() })))
        }
    }

    fun textCell(text: String, colSpan: Int = 1, rowSpan: Int = 1) {
        textCell(text, null, colSpan, rowSpan)
    }

    fun textCell(text: String, style: StyleAttributes? = null, colSpan: Int = 1, rowSpan: Int = 1) {
        textCell(style, colSpan, rowSpan) { +text }
    }

    fun textCell(style: StyleAttributes? = null, colSpan: Int = 1, rowSpan: Int = 1, block: F.() -> Unit) {
        if (style != null) {
            style(style) {
                textCell(null, colSpan, rowSpan, block)
            }
        } else {
            children += CellBuilder(colSpan, rowSpan, null, phraseBuilder().also { it.block() })
        }
    }

    fun tableCell(
        columns: Int,
        style: StyleAttributes? = null,
        weights: FloatArray? = null,
        margin: Margin? = Margin.ZERO,
        headerRows: Int = 0,
        colSpan: Int = 1,
        rowSpan: Int = 1,
        block: T.() -> Unit
    ) {
        children += CellBuilder(colSpan, rowSpan, margin, tableBuilder(TableAttributes(columns, 100f, weights, margin, headerRows), style).also { it.block() })
    }

    fun imageCell(resource: String, style: StyleAttributes? = null, colSpan: Int = 1, rowSpan: Int = 1, width: Float? = null, height: Float? = null,
                  rotation: Float? = null) {
        if (style != null) {
            style(style) {
                imageCell(resource, null, colSpan, rowSpan, width, height, rotation)
            }
        } else {
            children += CellBuilder(colSpan, rowSpan, null, ImageBuilder(resource, width, height, rotation))
        }
    }

    fun qrCodeCell(link: String, style: QrStyle, colSpan: Int = 1, rowSpan: Int = 1) {
        children += CellBuilder(colSpan, rowSpan, null, QrBuilder(link, style))
    }

    class ForBlock(
        val attributes: TableAttributes,
        style: StyleAttributes?
    ) : TableBuilder<PhraseBuilder.ForBlock, ForBlock>(attributes, style) {
        override val phraseBuilder: () -> PhraseBuilder.ForBlock = { PhraseBuilder.ForBlock() }
        override val childBuilder: () -> ForBlock = { ForBlock(TableAttributes(0, attributes.widthPercentage, null, attributes.margin, 0), style) }
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> ForBlock = { t, s -> ForBlock(t, s) }
    }

    class ForPage(
        val attributes: TableAttributes,
        style: StyleAttributes?
    ) : TableBuilder<PhraseBuilder.ForPage, ForPage>(attributes, style) {
        override val phraseBuilder: () -> PhraseBuilder.ForPage = { PhraseBuilder.ForPage() }
        override val childBuilder: () -> ForPage = { ForPage(TableAttributes(0, attributes.widthPercentage, null, attributes.margin, 0), style) }
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> ForPage = { t, s -> ForPage(t, s) }
    }
}