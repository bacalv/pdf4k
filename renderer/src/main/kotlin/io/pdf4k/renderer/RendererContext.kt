package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Image
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfWriter
import io.pdf4k.domain.Component
import io.pdf4k.domain.Page
import io.pdf4k.domain.Stationary
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.StyleAttributes.Companion.DEFAULT_STYLE
import io.pdf4k.renderer.ComponentRenderer.render
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class RendererContext(
    val mainDocument: Document,
    val mainDocumentWriter: PdfWriter,
    val contentBlocksDocument: Document,
    val contentBlocksDocumentWriter: PdfWriter,
    val loadedStationary: Map<Stationary, LoadedStationary>,
    val fontProvider: FontProvider
) {
    private val styleStack: Stack<StyleAttributes> = Stack()
    private val pageNumber: AtomicInteger = AtomicInteger()
    val stationaryByPage = mutableListOf<Pair<LoadedStationary, Int>>()

    init {
        styleStack.push(DEFAULT_STYLE)
    }

    fun peekStyle() = styleStack.peek() ?: DEFAULT_STYLE

    fun pushStyle(component: Component.Style) = pushStyle(component.styleAttributes)

    fun pushStyle(style: StyleAttributes?): StyleAttributes =
        styleStack.push(style?.let { styleStack.peek() + it } ?: styleStack.peek())

    fun popStyle(): StyleAttributes = styleStack.pop()

    fun currentFont() = with(peekStyle()) {
        fontProvider.getFont(font, size, fontStyle, colour)
    }

    fun drawBlocks(page: Page, stationary: Stationary) {
        page.blockContent.forEach { (blockName, content) ->
            stationary.blocks[blockName]?.let { block ->
                ColumnText(contentBlocksDocumentWriter.directContent).let { columnText ->
                    columnText.setSimpleColumn(block.x, block.y + block.h, block.x + block.w, block.y)
                    content.children.render(this).forEach { columnText.addElement(it) }
                    columnText.go()
                }
            }
        }
    }

    fun add(elements: List<Element>) {
        elements.forEach { contentBlocksDocument.add(it) }
    }

    fun nextPage(stationary: Stationary, blocksFilled: Int) {
        pageNumber.incrementAndGet()
        stationaryByPage += (loadedStationary[stationary]
            ?: throw IllegalStateException("Can't find stationary $stationary")) to blocksFilled
    }

    fun currentPageNumber() = pageNumber.get()

    fun getImage(resource: String, width: Float?, height: Float?, rotation: Float?): Image =
        Image.getInstanceFromClasspath("images/${resource}").also { img ->
            when {
                width != null && height != null -> img.scaleToFit(width, height)
                width != null -> img.scaleAbsoluteWidth(width)
                height != null -> img.scaleAbsoluteHeight(height)
            }
            rotation?.let { img.setRotationDegrees(it) }
        }
}
