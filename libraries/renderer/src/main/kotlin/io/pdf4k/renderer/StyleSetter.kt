package io.pdf4k.renderer

import com.lowagie.text.Chunk
import com.lowagie.text.Element
import com.lowagie.text.Element.*
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle.NO_BORDER
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import io.pdf4k.domain.*
import io.pdf4k.domain.StyleAttributes.Companion.DEFAULT_LEADING

object StyleSetter {
    fun Chunk.setStyle(context: RendererContext) {
        font = context.currentFont()

        context.peekStyle().let { style ->
            style.background?.let { setBackground(it) }
            takeIf { style.underlined == true }?.let {
                setUnderline(style.underlineColour, 1f, 0f, -2f, 0f, PdfContentByte.LINE_CAP_BUTT)
            }
        }
    }

    fun PdfPCell.setStyle(context: RendererContext) {
        context.peekStyle().let { style ->
            setHorizontalAlignment(this, style.align ?: HorizontalAlignment.Left)

            verticalAlignment = when (style.valign ?: VerticalAlignment.Top) {
                VerticalAlignment.Top -> ALIGN_TOP
                VerticalAlignment.Middle -> ALIGN_MIDDLE
                VerticalAlignment.Bottom -> ALIGN_BOTTOM
            }

            isUseAscender = true
            isUseDescender = true
            (style.leading ?: DEFAULT_LEADING).let { setLeading(it.fixed, it.multiplier) }
            style.cellBackground?.let { backgroundColor = it }
            (style.paddingLeft ?: 4f).let { paddingLeft = it }
            (style.paddingRight ?: 4f).let { paddingRight = it }
            (style.paddingTop ?: 4f).let { paddingTop = it }
            (style.paddingBottom ?: 4f).let { paddingBottom = it }
            style.borderColourTop?.let { borderColorTop = it }
            style.borderColourBottom?.let { borderColorBottom = it }
            style.borderColourLeft?.let { borderColorLeft = it }
            style.borderColourRight?.let { borderColorRight = it }
            style.borderWidthTop?.let { borderWidthTop = it }
            style.borderWidthBottom?.let { borderWidthBottom = it }
            style.borderWidthLeft?.let { borderWidthLeft = it }
            style.borderWidthRight?.let { borderWidthRight = it }
        }
    }

    private fun StyleAttributes.spacingBefore() = (size ?: 12f).let { size ->
        when (val s = spacingBefore) {
            null -> lineHeight()
            is Spacing.Fixed -> s.fixed
            is Spacing.Lines -> lineHeight() * s.lines
        }
    }

    private fun StyleAttributes.spacingAfter() = (size ?: 12f).let { size ->
        when (val s = spacingAfter) {
            null -> 0f
            is Spacing.Fixed -> s.fixed
            is Spacing.Lines -> lineHeight() * s.lines
        }
    }

    private fun StyleAttributes.lineHeight() = (leading ?: DEFAULT_LEADING).let { (fixed, multiplier) ->
        (size ?: 12f).let { size ->
            size + fixed + (size * (multiplier - 1f))
        }
    }

    fun PdfPTable.forParagraph(context: RendererContext, block: Paragraph.() -> Unit) {
        widthPercentage = 100.0f
        keepTogether = false
        context.peekStyle().let { style ->
            setSpacingBefore(style.spacingBefore())
            setSpacingAfter(style.spacingAfter())
            isSplitLate = style.splitLate ?: false
            isSplitRows = style.splitRows ?: true
            addCell(PdfPCell().also { cell ->
                cell.isUseAscender = true
                cell.isUseDescender = true
                cell.paddingLeft = 0f
                cell.paddingRight = 0f
                cell.paddingTop = 0f
                cell.paddingBottom = 0f
                cell.border = NO_BORDER
                setHorizontalAlignment(cell, style.align)
                (style.leading ?: DEFAULT_LEADING).let { cell.setLeading(it.fixed, it.multiplier) }
                cell.phrase = Paragraph().also { paragraph ->
                    paragraph.keepTogether = false
                    paragraph.block()
                }
            })
        }
    }

    fun PdfPTable.setStyle(context: RendererContext, component: Component.Table) {
        isExtendLastRow = component.extend ?: false
        widthPercentage = component.widthPercentage ?: 100f
        headerRows = component.headerRows ?: 0
        defaultCell.borderWidthTop = 0f
        defaultCell.borderWidthBottom = 0f
        defaultCell.borderWidthLeft = 0f
        defaultCell.borderWidthRight = 0f
        defaultCell.isUseAscender = true
        defaultCell.isUseDescender = true
        context.peekStyle().let { style ->
            setSpacingBefore(style.spacingBefore())
            setSpacingAfter(style.spacingAfter())
            isSplitLate = style.splitLate ?: false
            isSplitRows = style.splitRows ?: true
        }
        component.weights?.let { setWidths(it.toFloatArray()) }
    }

    fun newListTable() = ListTable().also { table ->
        table.widthPercentage = 100f
    }

    fun createListItem(context: RendererContext, phrase: Phrase, children: List<Element>): List<Element> {
        val listStyle = context.peekStyle().listStyle ?: ListStyle.Symbol()
        val symbol = Chunk(listStyle.getListSymbol(context.nextListItemNumber())).also { chunk -> chunk.setStyle(context) }
        context.listSymbolWidth(symbol.widthPoint)
        return listOf(
            listCell().also {
                it.addElement(
                    Paragraph().also { paragraph ->
                        paragraph.add(Phrase().also { phrase -> phrase.add(symbol) })
                        paragraph.alignment = ALIGN_RIGHT
                    }
                )
            },
            listCell().also { it.addElement(phrase) }
        )+ (children.takeIf { it.isNotEmpty() }?.let {
            children.filterIsInstance<PdfPTable>().map {
                listOf(listCell()) + listCell().also { nest -> nest.addElement(it) }
            }.flatten()
        } ?: emptyList())
    }

    fun listCell() = PdfPCell().also {
        it.borderWidthTop = 0f
        it.borderWidthBottom = 0f
        it.borderWidthLeft = 0f
        it.borderWidthRight = 0f
        it.isUseAscender = true
        it.isUseDescender = true
    }

    private fun setHorizontalAlignment(cell: PdfPCell, align: HorizontalAlignment?) {
        cell.horizontalAlignment = when (align ?: HorizontalAlignment.Left) {
            HorizontalAlignment.Left -> ALIGN_LEFT
            HorizontalAlignment.Center -> ALIGN_CENTER
            HorizontalAlignment.Right -> ALIGN_RIGHT
            HorizontalAlignment.Justified -> ALIGN_JUSTIFIED
            HorizontalAlignment.JustifiedAll -> ALIGN_JUSTIFIED_ALL
        }
    }
}