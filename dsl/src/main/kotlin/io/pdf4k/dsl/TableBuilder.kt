package io.pdf4k.dsl

import io.pdf4k.domain.*
import java.net.URI

@PdfDsl
abstract class TableBuilder<F : PhraseBuilder<F>, T : TableBuilder<F, T>>(
    private val attributes: TableAttributes,
    private val style: StyleAttributes?
) : BuildsCellStyle<Component.Table, T> {
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
        columns: Int,
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

    fun qrCodeCell(link: String, qrStyle: QrStyle, style: StyleAttributes? = null, colSpan: Int = 1, rowSpan: Int = 1) {
        if (style != null) {
            style(style) {
                qrCodeCell(link, qrStyle, null, colSpan, rowSpan)
            }
        } else {
            children += CellBuilder(colSpan, rowSpan, Margin.ZERO, QrBuilder(link, qrStyle))
        }
    }

    class ForBlock(
        val attributes: TableAttributes,
        style: StyleAttributes?
    ) : TableBuilder<PhraseBuilder.ForBlock, ForBlock>(attributes, style) {
        override val phraseBuilder: () -> PhraseBuilder.ForBlock = { PhraseBuilder.ForBlock() }
        override val childBuilder: () -> ForBlock = { ForBlock(TableAttributes(0, attributes.widthPercentage, null, attributes.margin, 0, attributes.extend), style) }
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> ForBlock = { t, s -> ForBlock(t, s) }
    }

    class ForPage(
        val attributes: TableAttributes,
        style: StyleAttributes?
    ) : TableBuilder<PhraseBuilder.ForPage, ForPage>(attributes, style) {
        override val phraseBuilder: () -> PhraseBuilder.ForPage = { PhraseBuilder.ForPage() }
        override val childBuilder: () -> ForPage = { ForPage(TableAttributes(0, attributes.widthPercentage, null, attributes.margin, 0, attributes.extend), style) }
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> ForPage = { t, s -> ForPage(t, s) }
    }
}