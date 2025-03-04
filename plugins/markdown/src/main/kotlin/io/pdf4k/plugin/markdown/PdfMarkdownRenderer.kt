package io.pdf4k.plugin.markdown

import io.pdf4k.domain.Font
import io.pdf4k.domain.ListStyle
import io.pdf4k.domain.Margin
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.ListBuilder
import io.pdf4k.dsl.PhraseBuilder
import io.pdf4k.dsl.TableBuilder
import org.commonmark.node.*
import org.commonmark.renderer.Renderer
import java.awt.Color
import java.awt.Color.BLUE

class Pdf4kDslMarkdownRenderer<F : PhraseBuilder<F>, T : TableBuilder<F, T>>(private val builder: T): Renderer {
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
//
//            is Heading -> {
//                builder.textCell(headingStyle(node.level)) {
//                    +(node.firstChild as Text).literal
//                }
//            }
//
//            is Paragraph -> {
//                builder.text(node, null)
//            }
//
//            is BlockQuote -> builder.blockQuote(node)

            else -> TODO("Unsupported node type ${node::class.simpleName}")
        }

        return ""
    }

    private fun blockQuote(builder: TableBuilder<F, T>, node: BlockQuote) = with(builder) {
        style(paddingLeft = 6f, borderWidthLeft = 2f, borderColourLeft = blockQuoteDark, cellBackground = blockQuoteLight) {
            tableCell(1, margin = Margin(0f, 0f, 6f, 0f)) {
                style(paddingLeft = 4f, borderWidthLeft = 0f, borderColourLeft = Color.BLACK) {
                    text(node.firstChild, null)
                }
            }
        }
    }

    private fun T.text(node: Node, currentFontStyle: Font.Style? = null) {
        var child: Node? = node

        while (child != null) {
            when (val c = child) {
                is Heading -> {
                    textCell(headingStyle(c.level)) {
                        +(c.firstChild as Text).literal
                        +"\n\n"
                    }
                }
                is Paragraph -> textCell { text(c.firstChild, currentFontStyle) }
                is BlockQuote -> blockQuote(this, c)
                is OrderedList -> listCell(style(listStyle = ListStyle.Numbered())) { listItems(c) }
                is BulletList -> listCell(style(listStyle = ListStyle.Symbol())) { listItems(c) }
                else -> TODO("UNKNOWN TYPE $c")
            }
            child = child.next
        }
    }

    private fun ListBuilder<F, T>.listItems(node: Node) {
        var child = node.firstChild

        while (child != null) {
            when (child) {
                is ListItem -> {
                    var next = child.firstChild
                    item {
                        text(next.firstChild)
                    }.let { item ->
                        next = next.next
                        while (next != null) {
                            when (next) {
                                is OrderedList -> {
                                    item.list(style(listStyle = ListStyle.Numbered())) { listItems(next) }
                                }

                                is BulletList -> {
                                    item.list(style(listStyle = ListStyle.Symbol())) { listItems(next) }
                                }

                                is BlockQuote -> {
                                    item.table {
                                        blockQuote(this, next as BlockQuote)
                                    }
                                }
                                else -> TODO("NOOOOOOb")
                            }
                            next = next.next
                        }
                    }
                }

                else -> TODO("UNKNOWN TYPE $child")
            }

            child = child.next
        }
    }

    private fun F.text(node: Node, currentFontStyle: Font.Style? = null) {
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

                is BlockQuote -> blockQuote(builder, c)
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