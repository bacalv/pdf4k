package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.FontFactory
import com.lowagie.text.Image
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfWriter
import io.pdf4k.domain.*
import io.pdf4k.domain.Font.BuiltIn.Ariel
import io.pdf4k.domain.Font.Style.*
import io.pdf4k.domain.StyleAttributes.Companion.DEFAULT_STYLE
import io.pdf4k.renderer.ComponentRenderer.render
import java.awt.Color
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import com.lowagie.text.Font as ITFont

class RendererContext(
    val mainDocument: Document,
    val contentBlocksDocument: Document,
    val contentBlocksDocumentWriter: PdfWriter,
    val loadedStationary: Map<Stationary, LoadedStationary>
) {
    private val styleStack: Stack<StyleAttributes> = Stack()
    private val pageNumber: AtomicInteger = AtomicInteger()
    val stationaryByPage = mutableListOf<Pair<LoadedStationary, Int>>()
    val baseFontCache = mutableMapOf<String, BaseFont>()

    init {
        styleStack.push(DEFAULT_STYLE)
    }

    fun peekStyle() = styleStack.peek() ?: DEFAULT_STYLE

    fun pushStyle(component: Component.Style) = pushStyle(component.styleAttributes)

    fun pushStyle(style: StyleAttributes?) =
        styleStack.push(style?.let { styleStack.peek() + it } ?: styleStack.peek())

    fun popStyle() = styleStack.pop()

    fun currentFont(): ITFont {
        with(peekStyle()) {
            val font = font ?: Ariel
            val size = size ?: 12f
            val colour = colour ?: Color.BLACK
            val fontStyle = fontStyle ?: Plain

            val name = when (font) {
                Ariel -> "arial unicode ms"
            }

            val style = when (fontStyle) {
                Plain -> ITFont.NORMAL
                Bold -> ITFont.BOLD
                Italic -> ITFont.ITALIC
                BoldItalic -> ITFont.BOLDITALIC
            }

            return baseFontCache[name]?.let { baseFont ->
                ITFont(baseFont, size, style, colour)
            } ?: run {
                val result = FontFactory.getFont(name, size, style, colour)
                baseFontCache[name] = result.baseFont
                result
            }
        }
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
