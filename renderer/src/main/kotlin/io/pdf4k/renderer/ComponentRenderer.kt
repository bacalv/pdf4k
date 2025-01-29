package io.pdf4k.renderer

import com.lowagie.text.*
import com.lowagie.text.Rectangle.NO_BORDER
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import io.pdf4k.domain.Component
import io.pdf4k.domain.StyleAttributes.Companion.DEFAULT_LEADING
import io.pdf4k.renderer.StyleSetter.setHorizontalAlignment
import io.pdf4k.renderer.StyleSetter.setStyle

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
                table.keepTogether = false
                context.peekStyle().let { style ->
                    table.isSplitLate = style.splitLate ?: false
                    table.isSplitRows = style.splitRows ?: true
                }
                table.addCell(PdfPCell().also { cell ->
                    cell.paddingLeft = 0f
                    cell.paddingRight = 0f
                    cell.paddingTop = 0f
                    cell.paddingBottom = 0f
                    cell.border = NO_BORDER
                    setHorizontalAlignment(cell, context.peekStyle().align)
                    context.currentLeading().let { cell.setLeading(it.fixed, it.multiplier) }
                    cell.phrase = Paragraph().also { paragraph ->
                        paragraph.keepTogether = false
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
                table.isExtendLastRow = component.extend
                table.widthPercentage = component.widthPercentage ?: 100f
                table.defaultCell.borderWidthTop = 0f
                table.defaultCell.borderWidthBottom = 0f
                table.defaultCell.borderWidthLeft = 0f
                table.defaultCell.borderWidthRight = 0f
                context.pushStyle(component.style)
                context.peekStyle().let { style ->
                    table.isSplitLate = style.splitLate ?: false
                    table.isSplitRows = style.splitRows ?: true
                }
                component.weights?.let { table.setWidths(it) }
                table.headerRows = component.headerRows
                component.children.render(context).filterIsInstance<PdfPCell>().forEach { table.addCell(it) }
                context.popStyle()
                table.completeRow()
            })

            is Component.Cell -> listOf(when (component) {
                is Component.Cell.Text -> component.phrase.render<Phrase>(context)
                is Component.Cell.Table -> component.table.render<PdfPTable>(context)
                is Component.Cell.Image -> component.image.render<Image>(context)
                is Component.Cell.QrCode -> component.qrCode.render(context)
            }.let { element ->
                when (element) {
                    is Phrase -> PdfPCell(element).also { it.setStyle(context) }
                    is PdfPTable -> PdfPCell(element).also {
                        it.setStyle(context)
                        (component as Component.Cell.Table).margin.let { margin ->
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
}
