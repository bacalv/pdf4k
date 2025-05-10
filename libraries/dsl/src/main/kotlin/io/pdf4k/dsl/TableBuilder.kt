package io.pdf4k.dsl

import io.pdf4k.domain.*
import java.net.URI

@PdfDsl
abstract class TableBuilder<F : PhraseBuilder<F>, P : ParagraphBuilder<F, P>, T : TableBuilder<F, P, T, C>, C : ContentBuilder<F, P, T, C>>(
    private val attributes: TableAttributes,
    private val style: StyleAttributes?
) : BuildsStyle<Component.Table, T> {
    override val children: MutableList<ComponentBuilder<*, *>> = mutableListOf()
    abstract val phraseBuilder: () -> F
    abstract val tableBuilder: (TableAttributes, StyleAttributes?) -> T

    override fun build() = with(attributes) {
        Component.Table(columns, widthPercentage, weights, headerRows, extend, style, children.map { it.build() })
    }

    fun textCell(text: String, style: StyleAttributes? = null, colSpan: Int? = null, rowSpan: Int? = null) {
        textCell(style, colSpan, rowSpan) { +text }
    }

    fun textCell(style: StyleAttributes? = null, colSpan: Int? = null, rowSpan: Int? = null, block: F.() -> Unit) {
        if (style != null) {
            style(style) {
                textCell(null, colSpan, rowSpan, block)
            }
        } else {
            children += CellBuilder(colSpan, rowSpan, Margin.ZERO, phraseBuilder().also { it.block() })
        }
    }

    fun tableCell(
        columns: Int = 1,
        style: StyleAttributes? = null,
        weights: List<Float>? = null,
        margin: Margin = Margin.ZERO,
        headerRows: Int? = null,
        extend: Boolean? = null,
        colSpan: Int? = null,
        rowSpan: Int? = null,
        block: T.() -> Unit
    ) {
        children += CellBuilder(colSpan, rowSpan, margin, tableBuilder(TableAttributes(columns, 100f, weights, margin, headerRows, extend), style).also { it.block() })
    }

    fun imageCell(resource: URI, style: StyleAttributes? = null, colSpan: Int = 1, rowSpan: Int = 1, width: Float? = null, height: Float? = null,
                  rotation: Float? = null) {
        imageCell(ResourceLocation.Remote.Uri(resource), style, colSpan, rowSpan, width, height, rotation)
    }

    fun imageCell(resource: String, style: StyleAttributes? = null, colSpan: Int = 1, rowSpan: Int = 1, width: Float? = null, height: Float? = null,
                  rotation: Float? = null) {
        imageCell(ResourceLocation.Local(resource), style, colSpan, rowSpan, width, height, rotation)
    }

    fun imageCell(resourceLocation: ResourceLocation, style: StyleAttributes? = null, colSpan: Int = 1, rowSpan: Int = 1, width: Float? = null, height: Float? = null,
                  rotation: Float? = null) {
        if (style != null) {
            style(style) {
                imageCell(resourceLocation, null, colSpan, rowSpan, width, height, rotation)
            }
        } else {
            children += CellBuilder(colSpan, rowSpan, Margin.ZERO, ImageBuilder(resourceLocation, width, height, rotation))
        }
    }

    fun listCell(style: StyleAttributes? = null, block: ListBuilder<F, P, T, C>.() -> Unit) {
        if (style != null) {
            style(style) { listCell(null, block) }
        } else {
            addChild(ListBuilder(phraseBuilder, tableBuilder).also { it.block() })
        }
    }

    fun cell(style: StyleAttributes? = null, colSpan: Int = 1, rowSpan: Int = 1, block: ContentBuilder.ForCell.() -> Unit) {
        if (style != null) {
            style(style) { cell(null, colSpan, rowSpan, block) }
        } else {
            addChild(CellBuilder(colSpan, rowSpan, Margin.ZERO, ContentBuilder.ForCell().apply(block)))
        }
    }

    class ForBlock(
        private val attributes: TableAttributes,
        style: StyleAttributes?
    ) : TableBuilder<PhraseBuilder.ForBlock, ParagraphBuilder.ForBlock, ForBlock, ContentBuilder.ForBlock>(attributes, style) {
        override val phraseBuilder: () -> PhraseBuilder.ForBlock = { PhraseBuilder.ForBlock() }
        override val childBuilder: () -> ForBlock = { ForBlock(TableAttributes(0, attributes.widthPercentage, null, attributes.margin, 0, attributes.extend), style) }
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> ForBlock = { t, s -> ForBlock(t, s) }
    }

    class ForSection(
        private val attributes: TableAttributes,
        style: StyleAttributes?
    ) : TableBuilder<PhraseBuilder.ForSection, ParagraphBuilder.ForSection, ForSection, ContentBuilder.ForSection>(attributes, style) {
        override val phraseBuilder: () -> PhraseBuilder.ForSection = { PhraseBuilder.ForSection() }
        override val childBuilder: () -> ForSection = { ForSection(TableAttributes(0, attributes.widthPercentage, null, attributes.margin, 0, attributes.extend), style) }
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> ForSection = { t, s -> ForSection(t, s) }
    }

    class ForCell(
        private val attributes: TableAttributes,
        style: StyleAttributes?
    ) : TableBuilder<PhraseBuilder.ForCell, ParagraphBuilder.ForCell, ForCell, ContentBuilder.ForCell>(attributes, style) {
        override val phraseBuilder: () -> PhraseBuilder.ForCell = { PhraseBuilder.ForCell() }
        override val childBuilder: () -> ForCell = { ForCell(TableAttributes(0, attributes.widthPercentage, null, attributes.margin, 0, attributes.extend), style) }
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> ForCell = { t, s -> ForCell(t, s) }
    }
}

typealias AnyTableBuilder = TableBuilder<*, *, *, *>
