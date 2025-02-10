package io.pdf4k.plugin.markdown

import io.pdf4k.domain.Font
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.ContentBuilder
import io.pdf4k.dsl.PhraseBuilder
import org.commonmark.node.*
import org.commonmark.renderer.Renderer
import java.awt.Color

class PdfMarkdownRenderer(private val builder: ContentBuilder<*, *, *, *>): Renderer {
    companion object {
        private val blockQuoteDark = Color(127, 127, 127)
        private val blockQuoteLight = Color(240, 240, 240)
    }

    override fun render(node: Node, out: Appendable) {
        render(node)
    }

    override fun render(node: Node): String {
        when (node) {
            is Document -> node.forEachChild { render(it) }

            is Heading -> {
                builder.paragraph(headingStyle(node.level)) {
                    +(node.firstChild as Text).literal
                }
            }

            is Paragraph -> builder.paragraph { phrase { text(node) } }

            is BlockQuote -> builder.table(2, weights = listOf(1f, 49f)) {
                textCell(style(cellBackground = blockQuoteDark)) { +"" }
                textCell(style(cellBackground = blockQuoteLight)) {
                    text(node)
                }
            }
            else -> TODO("Unsupported node type ${node::class.simpleName}")
        }

        return ""
    }

    private fun PhraseBuilder<*>.text(node: Node) {
        text(node, null)
    }

    private fun PhraseBuilder<*>.text(node: Node, currentFontStyle: Font.Style? = null) {
        var child = node.firstChild

        while (child != null) {
            when (child) {
                is Text -> +child.literal
                is Emphasis -> (currentFontStyle + Font.Style.Italic).let {
                    style(fontStyle = it) {
                        text(child, it)
                    }
                }

                is StrongEmphasis -> (currentFontStyle + Font.Style.Bold).let {
                    style(fontStyle = it) {
                        text(child, it)
                    }
                }

                is SoftLineBreak -> crlf()

                is Paragraph -> text(child, currentFontStyle)

                else -> TODO("Unsupported node type ${child::class.simpleName}")
            }
            child = child.next
        }

    }

    private fun headingStyle(level: Int) = style(
        size = when (level) {
            1 -> 32f
            2 -> 28f
            3 -> 24f
            4 -> 20f
            5 -> 16f
            else -> 12f
        }
    )

    private operator fun Font.Style?.plus(other: Font.Style): Font.Style = when (this) {
        null -> other
        Font.Style.Plain -> other
        Font.Style.Bold -> if (other == Font.Style.Italic) { Font.Style.BoldItalic } else Font.Style.Bold
        Font.Style.Italic -> if (other == Font.Style.Bold) { Font.Style.BoldItalic } else Font.Style.Italic
        Font.Style.BoldItalic -> this
    }

    private fun Node.forEachChild(block: (Node) -> Unit) {
        var child = firstChild
        while(child != null) {
            block(child)
            child = child.next
        }
    }
}