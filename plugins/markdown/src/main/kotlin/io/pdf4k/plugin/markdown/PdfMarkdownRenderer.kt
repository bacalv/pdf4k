package io.pdf4k.plugin.markdown

import io.pdf4k.domain.Font
import io.pdf4k.domain.Margin
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.AnyTableBuilder
import io.pdf4k.dsl.PhraseBuilder
import org.commonmark.node.*
import org.commonmark.renderer.Renderer
import java.awt.Color
import java.awt.Color.BLUE

class Pdf4kDslMarkdownRenderer(private val builder: AnyTableBuilder): Renderer {
    companion object {
        private val blockQuoteDark = Color(127, 127, 127)
        private val blockQuoteLight = Color(240, 240, 240)
        private val linkStyle = style(underlined = true, colour = BLUE)
    }

    override fun render(node: Node, out: Appendable) {
        render(node)
    }

    override fun render(node: Node): String {
        when (node) {
            is Document -> {
                builder.text(node.firstChild)
            }

            is Heading -> {
                builder.textCell(headingStyle(node.level)) {
                    +(node.firstChild as Text).literal
                }
            }

            is Paragraph -> {
                builder.text(node, null)
            }

            is BlockQuote -> builder.blockQuote(node)

            else -> TODO("Unsupported node type ${node::class.simpleName}")
        }

        return ""
    }

    private fun AnyTableBuilder.blockQuote(node: BlockQuote) {
        style(paddingLeft = 14f, borderWidthLeft = 10f, borderColourLeft = blockQuoteDark, cellBackground = blockQuoteLight) {
            tableCell(1, margin = Margin(0f, 0f, 14f, 0f)) {
                style(paddingLeft = 4f, borderWidthLeft = 0f, borderColourLeft = Color.BLACK) {
                    text(node.firstChild, null)
                }
            }
        }
    }

    private fun AnyTableBuilder.text(node: Node, currentFontStyle: Font.Style? = null) {
        var child: Node? = node

        while (child != null) {
            when (val c = child) {
                is Heading -> {
                    textCell(headingStyle(c.level)) {
                        +(c.firstChild as Text).literal
                    }
                }
                is Paragraph -> textCell { text(c.firstChild, currentFontStyle) }
                is BlockQuote -> blockQuote(c)
                else -> TODO("UNKNOWN TYPE $c")
            }
            child = child.next
        }
    }

    private fun PhraseBuilder<*>.text(node: Node, currentFontStyle: Font.Style? = null) {
        var child: Node? = node
        while (child != null) {
            when (val c = child) {
                is Text -> +c.literal
                is Emphasis -> (currentFontStyle + Font.Style.Italic).let {
                    style(fontStyle = it) {
                        text(c.firstChild, it)
                    }
                }

                is StrongEmphasis -> (currentFontStyle + Font.Style.Bold).let {
                    style(fontStyle = it) {
                        text(c.firstChild, it)
                    }
                }

                is SoftLineBreak -> crlf()

                is Paragraph -> text(c.firstChild, currentFontStyle)

                is Link -> link(c.destination) {
                    style(linkStyle) {
                        text(c.firstChild, currentFontStyle)
                    }
                }

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
        null,
        Font.Style.Plain -> other
        Font.Style.Bold -> if (other == Font.Style.Italic) { Font.Style.BoldItalic } else Font.Style.Bold
        Font.Style.Italic -> if (other == Font.Style.Bold) { Font.Style.BoldItalic } else Font.Style.Italic
        Font.Style.BoldItalic -> this
    }
}