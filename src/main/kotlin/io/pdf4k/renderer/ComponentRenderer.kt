package io.pdf4k.renderer

import com.lowagie.text.*
import com.lowagie.text.Element.*
import com.lowagie.text.Rectangle.NO_BORDER
import com.lowagie.text.pdf.PdfContentByte
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import io.pdf4k.domain.Component
import io.pdf4k.domain.HorizontalAlignment
import io.pdf4k.domain.StyleAttributes.Companion.DEFAULT_LEADING
import io.pdf4k.domain.VerticalAlignment

object ComponentRenderer {
    fun List<Component>.render(context: RendererContext): List<Element> = map { component ->
        when (component) {
            is Component.Content -> {
                component.children.render(context)
            }

            is Component.Style -> {
                context.pushStyle(component)
                component.children.render(context).also { context.popStyle() }
            }

            is Component.Paragraph -> listOf(PdfPTable(1).also { table ->
                table.widthPercentage = 100.0f
                table.addCell(PdfPCell().also { cell ->
                    cell.paddingLeft = 0f
                    cell.paddingRight = 0f
                    cell.paddingTop = 0f
                    cell.paddingBottom = 0f
                    cell.border = NO_BORDER
                    cell.setHorizintalAlignment(context.peekStyle().align)
                    context.currentLeading().let { cell.setLeading(it.fixed, it.multiplier) }
                    cell.phrase = Paragraph().also { paragraph ->
                        paragraph.addAll(component.children.render(context))
                    }
                })
            })

            is Component.Phrase -> listOf(Phrase().also { phrase ->
                val renderedChildren = component.children.render(context).map { element ->
                    when (element) {
                        is Image -> Chunk(element, 0f, 0f)
                        else -> element
                    }
                }
                phrase.addAll(renderedChildren)
            })

            is Component.Chunk -> listOf(Chunk(component.text).also { it.setStyle(context) })

            is Component.Link -> listOf(Anchor().also {
                it.reference = component.target
                it.add(Chunk(component.text).also { it.setStyle(context) })
            })

            is Component.Anchor -> listOf(Anchor().also {
                it.name = component.name
                it.addAll(component.children.render(context))
            })

            is Component.PageNumber -> listOf(Chunk(context.currentPageNumber().toString()).also { chunk ->
                chunk.setStyle(context)
            })

            is Component.Image -> listOf(context.getImage(component.resource, component.width, component.height, component.rotation))

            is Component.QrCode -> listOf(getQrCode(component))

            is Component.Table -> listOf(PdfPTable(component.columns).also { table ->
                table.widthPercentage = component.widthPercentage ?: 100f
                table.defaultCell.borderWidthTop = 0f
                table.defaultCell.borderWidthBottom = 0f
                table.defaultCell.borderWidthLeft = 0f
                table.defaultCell.borderWidthRight = 0f
                component.weights?.let { table.setWidths(it) }
                table.headerRows = component.headerRows
                component.children.render(context).forEach {
                    when (it) {
                        is PdfPCell -> table.addCell(it)
                    }
                }
                table.completeRow()
            })

            is Component.Cell -> listOf(when (component) {
                is Component.Cell.Style -> component.style.render(context)
                is Component.Cell.Text -> component.phrase.render<Phrase>(context)
                is Component.Cell.Table -> component.table.render<PdfPTable>(context)
                is Component.Cell.Image -> component.image.render<Image>(context)
                is Component.Cell.QrCode -> component.qrCode.render(context)
            }.let { element ->
                when (element) {
                    is Phrase -> PdfPCell(element).also { it.setStyle(context) }
                    is PdfPTable -> PdfPCell(element).also {
                        it.setStyle(context)
                        (component as Component.Cell.Table).margin?.let { margin ->
                            it.paddingLeft = margin.left
                            it.paddingRight = margin.right
                            it.paddingTop = margin.top
                            it.paddingBottom = margin.bottom
                        }
                    }
                    is Image -> PdfPCell(element).also { it.setStyle(context) }
                    else -> throw IllegalStateException("Unexpected element: " + element::class.simpleName)
                }.also { cell ->
                    cell.colspan = component.colSpan
                    cell.rowspan = component.rowSpan
                }
            })
        }
    }.flatten()

    private fun getQrCode(component: Component.QrCode) =
        Image.getInstance(QrRenderer.render(component.link, component.style)).also {
            it.scaleToFit(component.style.size.toFloat(), component.style.size.toFloat())
        }

    private inline fun <reified T : Element> Component.render(context: RendererContext): T =
        listOf(this).render(context).first() as T

    private fun RendererContext.currentLeading() = peekStyle().leading ?: DEFAULT_LEADING

    private fun Chunk.setStyle(context: RendererContext) {
        font = context.currentFont()

        context.peekStyle().let { style ->
            style.background?.let { setBackground(it) }
            takeIf { style.underlined == true }?.let {
                setUnderline(style.underlineColour, 1f, 0f, -2f, 0f, PdfContentByte.LINE_CAP_BUTT)
            }
        }
    }

    private fun PdfPCell.setStyle(context: RendererContext) {
        context.peekStyle().let { style ->
            setHorizintalAlignment(style.align ?: HorizontalAlignment.Left)

            verticalAlignment = when(style.valign) {
                VerticalAlignment.Top -> ALIGN_TOP
                VerticalAlignment.Middle -> ALIGN_MIDDLE
                VerticalAlignment.Bottom -> ALIGN_BOTTOM
                null -> verticalAlignment
            }

            if (style.leading != null) { setLeading(style.leading.fixed, style.leading.multiplier) }
            if (style.cellBackground != null) backgroundColor = style.cellBackground
            if (style.paddingLeft != null) paddingLeft = style.paddingLeft
            if (style.paddingRight != null) paddingRight = style.paddingRight
            if (style.paddingTop != null) paddingTop = style.paddingTop
            if (style.paddingBottom != null) paddingBottom = style.paddingBottom
            if (style.borderColourTop != null) borderColorTop = style.borderColourTop
            if (style.borderColourBottom != null) borderColorBottom = style.borderColourBottom
            if (style.borderColourLeft != null) borderColorLeft = style.borderColourLeft
            if (style.borderColourRight != null) borderColorRight = style.borderColourRight
            if (style.borderWidthTop != null) borderWidthTop = style.borderWidthTop
            if (style.borderWidthBottom != null) borderWidthBottom = style.borderWidthBottom
            if (style.borderWidthLeft != null) borderWidthLeft = style.borderWidthLeft
            if (style.borderWidthRight != null) borderWidthRight = style.borderWidthRight
        }
    }

    private fun PdfPCell.setHorizintalAlignment(align: HorizontalAlignment?) {
        horizontalAlignment = when (align) {
            HorizontalAlignment.Left -> ALIGN_LEFT
            HorizontalAlignment.Center -> ALIGN_CENTER
            HorizontalAlignment.Right -> ALIGN_RIGHT
            HorizontalAlignment.Justified -> ALIGN_JUSTIFIED
            HorizontalAlignment.JustifiedAll -> ALIGN_JUSTIFIED_ALL
            null -> horizontalAlignment
        }
    }
}
