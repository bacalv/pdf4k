package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.FontFactory
import com.lowagie.text.Image
import com.lowagie.text.pdf.PdfWriter
import io.pdf4k.domain.Component
import io.pdf4k.domain.Font.Style.*
import io.pdf4k.domain.Stationary
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.StyleAttributes.Companion.DEFAULT_STYLE
import java.awt.Color
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import com.lowagie.text.Font as ITFont

class RendererContext(
    val mainDocument: Document,
    val mainDocumentWriter: PdfWriter,
    val contentBlocksDocument: Document,
    val contentBlocksDocumentWriter: PdfWriter,
    val loadedStationary: Map<String, LoadedStationary>
) {
    private val styleStack: Stack<StyleAttributes> = Stack()
    private val pageNumber: AtomicInteger = AtomicInteger()
    val stationaryByPage = mutableListOf<Pair<LoadedStationary, Int>>()

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
            val font = font ?: io.pdf4k.domain.Font.BuiltIn.Ariel
            val size = size ?: 12f
            val colour = colour ?: Color.BLACK
            val fontStyle = fontStyle ?: Plain

            val name = when (font) {
                io.pdf4k.domain.Font.BuiltIn.Ariel -> "arial unicode ms"
            }

            val style = when (fontStyle) {
                Plain -> ITFont.NORMAL
                Bold -> ITFont.BOLD
                Italic -> ITFont.ITALIC
                BoldItalic -> ITFont.BOLDITALIC
            }
            return FontFactory.getFont(name, size, style, colour)
        }
    }

    fun add(elements: List<Element>) {
        elements.forEach { contentBlocksDocument.add(it) }
    }

    fun nextPage(stationary: Stationary, blocksFilled: Int) {
        pageNumber.incrementAndGet()
        stationaryByPage += (loadedStationary[stationary.template]
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
