package io.pdf4k.renderer

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import io.pdf4k.domain.Component
import io.pdf4k.renderer.StyleSetter.forParagraph
import io.pdf4k.renderer.StyleSetter.setStyle

object ComponentRenderer {
    fun List<Component>.render(context: RendererContext): List<Element> = map { component ->
        when (component) {
            is Component.Content -> component.children.render(context)

            is Component.Style -> {
                context.pushStyle(component)
                component.children.render(context).also { context.popStyle() }
            }

            is Component.Paragraph -> listOf(PdfPTable(1).also { table ->
                table.forParagraph(context) { addAll(component.children.render(context)) }
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

            is Component.Image -> listOf(
                context.getImage(component.resource, component.width, component.height, component.rotation)
            )

            is Component.QrCode -> listOf(context.getQrCode(component))

            is Component.Table -> listOf(PdfPTable(component.columns).also { table ->
                context.pushStyle(component.style)
                table.setStyle(context, component)
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

    private inline fun <reified T : Element> Component.render(context: RendererContext): T =
        listOf(this).render(context).first() as T
}
