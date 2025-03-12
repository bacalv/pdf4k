package io.pdf4k.plugin.markdown

import io.pdf4k.domain.Component
import io.pdf4k.domain.Font
import io.pdf4k.domain.ListStyle
import io.pdf4k.domain.StyleAttributes.Companion.border
import io.pdf4k.domain.StyleAttributes.Companion.padding
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.AnyContentBuilder
import io.pdf4k.dsl.ListBuilder
import io.pdf4k.dsl.PhraseBuilder
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.node.*
import org.commonmark.renderer.Renderer
import java.awt.Color
import java.awt.Color.BLUE

class Pdf4kDslMarkdownRenderer(private val builder: AnyContentBuilder, private val images: Map<String, Component.Image>) : Renderer {
    companion object {
        private val dark = Color(127, 127, 127)
        private val light = Color(240, 240, 240)
        private val transparent = Color(0, 0, 0, 0)
        private val linkStyle = style(underlined = true, colour = BLUE)
    }

    override fun render(node: Node, out: Appendable) {
        render(node)
    }

    override fun render(node: Node): String {
        builder.style(spacingBefore = 24f) {
            text(node.firstChild)
        }
        return ""
    }

    private fun tableBlock(builder: AnyContentBuilder, node: TableBlock) = with(builder) {
        val rows = node.firstChild.childList<TableRow>() + node.lastChild.childList<TableRow>()
        val columns = node.firstChild.firstChild.childList<TableCell>().size
        table(columns, headerRows = 1, style = border(1f) + padding(4f)) {
            rows.forEachIndexed { i, row ->
                style(cellBackground = if (i == 0) light else transparent) {
                    row.childList<TableCell>().forEach { cell ->
                        cell { text(cell.firstChild) }
                    }
                }
            }
        }
    }

    private inline fun <reified N : Node> Node.childList(): List<N> {
        val result = mutableListOf<N>()
        var child = firstChild

        while (child != null) {
            result += child as N
            child = child.next
        }

        return result
    }

    private fun AnyContentBuilder.blockQuote(node: BlockQuote) {
        style(
            paddingLeft = 6f,
            paddingTop = 4f,
            paddingRight = 4f,
            paddingBottom = 4f,
            borderWidthLeft = 2f,
            borderColourLeft = dark,
            cellBackground = light
        ) {
            table {
                cell {
                    style(paddingLeft = 4f, borderWidthLeft = 0f, borderColourLeft = Color.BLACK) {
                        text(node.firstChild, null)
                    }
                }
            }
        }
    }

    private fun AnyContentBuilder.text(node: Node, currentFontStyle: Font.Style? = null) {
        var child: Node? = node

        while (child != null) {
            when (val c = child) {
                is Heading -> style(headingStyle(c.level)) {
                    +(c.firstChild as Text).literal
                }
                is Paragraph -> phrase { text(c.firstChild, currentFontStyle) }
                is BlockQuote -> blockQuote(c)
                is OrderedList -> table {
                    listCell(style(listStyle = ListStyle.Numbered())) { listItems(c) }
                }
                is BulletList -> table {
                    listCell(style(listStyle = ListStyle.Symbol())) { listItems(c) }
                }
                is TableBlock -> tableBlock(this, c)
                is IndentedCodeBlock -> codeBlock(this, c)
                is Text -> phrase { text(c) }
                else -> TODO("UNKNOWN TYPE $c")
            }
            child = child.next
        }
    }

    private fun codeBlock(builder: AnyContentBuilder, code: IndentedCodeBlock) {
        builder.table {
            textCell(style(Font.BuiltIn.Courier, cellBackground = light)) {
                +code.literal
            }
        }
    }

    private fun ListBuilder<*, *, *, *>.listItems(node: Node) {
        var child = node.firstChild

        while (child != null) {
            when (child) {
                is ListItem -> {
                    var next = child.firstChild
                    item {
                        text(next.firstChild, null)
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
                                        cell {
                                            blockQuote(next as BlockQuote)
                                        }
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

                is SoftLineBreak -> +" "

                is Paragraph -> text(c.firstChild, currentFontStyle)

                is Link -> link(c.destination) {
                    style(linkStyle) {
                        text(c.firstChild, currentFontStyle)
                    }
                }

//                is BlockQuote -> blockQuote(c)

                is Code -> style(Font.BuiltIn.Courier) { +c.literal }

                is Image -> images[c.destination]?.let { img ->
                    image(img.resource, img.width, img.height, img.rotation)
                } ?: throw IllegalArgumentException("Image not found: ${c.destination}")
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

        Font.Style.Bold -> if (other == Font.Style.Italic) {
            Font.Style.BoldItalic
        } else Font.Style.Bold

        Font.Style.Italic -> if (other == Font.Style.Bold) {
            Font.Style.BoldItalic
        } else Font.Style.Italic

        Font.Style.BoldItalic -> this
    }
}