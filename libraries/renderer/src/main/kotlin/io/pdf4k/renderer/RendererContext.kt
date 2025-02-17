package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Image
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfWriter
import io.pdf4k.domain.*
import io.pdf4k.provider.ResourceLocators
import io.pdf4k.renderer.ComponentRenderer.render
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class RendererContext(
    val mainDocument: Document,
    val mainDocumentWriter: PdfWriter,
    val contentBlocksDocument: Document,
    val contentBlocksDocumentWriter: PdfWriter,
    val loadedStationary: Map<Stationary, LoadedStationary>,
    val resourceLocators: ResourceLocators
) {
    private val styleStack: Stack<StyleAttributes> = Stack()
    private val pageNumber: AtomicInteger = AtomicInteger()
    val stationaryByPage = mutableListOf<Pair<LoadedStationary, Int>>()

    init {
        styleStack.push(StyleAttributes())
    }

    fun peekStyle(): StyleAttributes = styleStack.peek()

    fun pushStyle(component: Component.Style) = pushStyle(component.styleAttributes)

    fun pushStyle(style: StyleAttributes?): StyleAttributes = peekStyle().let { current ->
        if (style == null) {
            styleStack.push(current)
        } else {
            styleStack.push(current + style)
        }
    }

    fun popStyle(): StyleAttributes = styleStack.pop()

    fun currentFont() = with(peekStyle()) {
        resourceLocators.fontProvider.getFont(font, size, fontStyle, colour)
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
        val loaded = loadedStationary[stationary] ?: throw IllegalStateException("Stationary not found")
        stationaryByPage += loaded to blocksFilled
    }

    fun currentPageNumber() = pageNumber.get()

    fun getImage(resource: ResourceLocation, width: Float?, height: Float?, rotation: Float?): Image =
        resourceLocators.imageResourceLocator.load(resource)
            .let { stream -> Image.getInstance(stream.readAllBytes()) }
            .also { img ->
                when {
                    width != null && height != null -> img.scaleToFit(width, height)
                    width != null -> img.scaleAbsoluteWidth(width)
                    height != null -> img.scaleAbsoluteHeight(height)
                }
                rotation?.let { img.setRotationDegrees(it) }
            }

    fun getBlockPageMapping() = stationaryByPage.mapIndexed { pageNumber, (_, blocksFilled) ->
        pageNumber to blocksFilled
    }.map { (pageNumber, blocksFilled) ->
        (0 until blocksFilled).map { pageNumber }
    }.flatten()
}
