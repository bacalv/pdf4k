package io.pdf4k.renderer

import com.lowagie.text.Chunk
import com.lowagie.text.Element.*
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import io.pdf4k.domain.HorizontalAlignment
import io.pdf4k.domain.StyleAttributes.Companion.DEFAULT_LEADING
import io.pdf4k.domain.VerticalAlignment

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

            verticalAlignment = when(style.valign ?: VerticalAlignment.Top) {
                VerticalAlignment.Top -> ALIGN_TOP
                VerticalAlignment.Middle -> ALIGN_MIDDLE
                VerticalAlignment.Bottom -> ALIGN_BOTTOM
            }

            (style.leading ?: DEFAULT_LEADING).let { setLeading(it.fixed, it.multiplier) }
            style.cellBackground?.let { backgroundColor = it }
            style.paddingLeft?.let { paddingLeft = it }
            style.paddingRight?.let { paddingRight = it }
            style.paddingTop?.let { paddingTop = it }
            style.paddingBottom?.let { paddingBottom = it }
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

    fun setHorizontalAlignment(cell: PdfPCell, align: HorizontalAlignment?) {
        cell.horizontalAlignment = when (align ?: HorizontalAlignment.Left) {
            HorizontalAlignment.Left -> ALIGN_LEFT
            HorizontalAlignment.Center -> ALIGN_CENTER
            HorizontalAlignment.Right -> ALIGN_RIGHT
            HorizontalAlignment.Justified -> ALIGN_JUSTIFIED
            HorizontalAlignment.JustifiedAll -> ALIGN_JUSTIFIED_ALL
        }
    }
}