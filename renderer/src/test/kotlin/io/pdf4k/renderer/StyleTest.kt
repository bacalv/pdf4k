package io.pdf4k.renderer

import io.pdf4k.domain.Font.*
import io.pdf4k.domain.Font.Custom.Resource
import io.pdf4k.domain.Font.Included.Arial
import io.pdf4k.domain.Font.Style.*
import io.pdf4k.domain.HorizontalAlignment.Center
import io.pdf4k.domain.Stationary
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.VerticalAlignment.Middle
import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.dsl.StationaryBuilder.Companion.plusBlocks
import io.pdf4k.testing.AbstractPdfApproverTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.Color.*

class StyleTest : AbstractPdfApproverTest() {
    @Test
    fun `renders different colour text`(approver: PdfApprover) {
        content {
            phrase(style(font = Arial, fontStyle = Bold, size = 16f)) {
                "RED" and style(colour = RED)
                " GREEN" and style(colour = GREEN)
                " BLUE" and style(colour = BLUE)
            }
        }.approve(approver)
    }

    @Test
    fun `renders different colour text backgrounds`(approver: PdfApprover) {
        content {
            phrase(style(font = Arial, fontStyle = Bold, size = 16f, colour = WHITE)) {
                "RED" and style(background = RED)
                +" "
                "GREEN" and style(background = GREEN)
                +" "
                "BLUE" and style(background = BLUE)
            }
        }.approve(approver)
    }

    @Test
    fun `renders underlined text`(approver: PdfApprover) {
        content {
            phrase {
                +"Next word is "
                "underlined" and style(underlined = true)
                crlf()
                +"Next words are "
                "red and underlined in red" and style(colour = RED, underlined = true)
                crlf()
                +"Next words are "
                "red and underlined in blue" and style(colour = RED, underlined = true, underlineColour = BLUE)
            }
        }.approve(approver)
    }

    @Test
    fun `renders different font styles`(approver: PdfApprover) {
        content {
            phrase {
                +"Next word is "
                "bold" and style(fontStyle = Bold)
                crlf()
                +"Next word is "
                "italic" and style(fontStyle = Italic)
                crlf()
                +"Next words are "
                "bold italic" and style(fontStyle = BoldItalic)
            }
        }.approve(approver)
    }

    @Test
    fun `applies styles to elements at different levels`(approver: PdfApprover) {
        content {
            style(fontStyle = Bold) {
                paragraph {
                    +"Bold\n\n"
                    style(colour = BLUE) {
                        +"Bold Blue\n\n"
                    }
                    phrase {
                        style(colour = WHITE, background = BLACK) {
                            +"Bold White with Black Background"
                        }
                    }
                }
                paragraph("\n\n")
                style(size = 16f) {
                    +"Bold 16 outside a table\n\n"
                    table(3) {
                        textCell("Bold 16")
                        style(colour = RED) {
                            textCell("Bold 16 Red")
                            textCell {
                                style(underline = true) {
                                    +"Bold 16 Red Underlined "
                                    "Green" and style(background = GREEN)
                                }
                            }
                        }
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `applies styles to all levels when set at block level`(approver: PdfApprover) {
        pdf(style(fontStyle = Bold)) {
            page {
                content { +"Bold" }
            }

            page(style(colour = RED)) {
                content { +"Bold and red" }
            }

            page(style(colour = RED)) {
                content(style(underlined = true)) { +"Bold red and underlined" }
            }

            page {
                content(style(fontStyle = Italic)) {
                    paragraph(style(size = 16f)) {
                        +"Italic size 16\n\n"
                        phrase(style(colour = BLUE)) {
                            +"Italic size 16 blue\n\n"
                        }
                    }
                    table(3, style(size = 24f)) {
                        textCell("Italic 24 underlined", style(underlined = true))
                        textCell(style(cellBackground = YELLOW)) {
                            +"Italic 24 yellow background"
                        }
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders semi-transparent colours`(approver: PdfApprover) {
        val stationary = Stationary.BlankA4Portrait.plusBlocks {
            block("red", 0f, 0f, 395.92f, 642.88f)
            block("green", 200f, 0f, 395.92f, 642.88f)
            block("blue", 250f, 0f, 200f, 642.88f)
        }
        val redTransparent = style(cellBackground = Color(255, 0, 0, 128))
        val greenTransparent = style(cellBackground = Color(0, 255, 0, 128))
        val blueTransparent = style(cellBackground = Color(0, 0, 255, 128))
        pdf {
            page(stationary = stationary) {
                listOf(
                    "red" to redTransparent,
                    "green" to greenTransparent,
                    "blue" to blueTransparent,
                ).forEach { (blockName, style) ->
                    block(blockName) {
                        table(1, style + noBorder, extend = true) {
                            textCell(blockName.uppercase(), style(align = Center, valign = Middle))
                        }
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders different font families`(approver: PdfApprover) {
        content {
            listOf(BuiltIn::class, Included::class).forEach { subType ->
                paragraph {
                    (subType.simpleName ?: "UNKNOWN") and style(fontStyle = Bold)
                }
                crlf()
                table(2, weights = floatArrayOf(2f, 8f)) {
                    subType.sealedSubclasses.mapNotNull { it.objectInstance }.forEach { font ->
                        textCell { +(font::class.simpleName ?: "UNKNOWN") }
                        textCell { "The quick brown fox jumped over the lazy hen." and style(font = font) }
                    }
                }
                crlf()
            }
        }.approve(approver)
    }

    @Test
    fun `renders custom font`(approver: PdfApprover) {
        content {
            paragraph {
                "The quick brown fox jumped over the lazy hen." and style(
                    font = Resource("ArianaVioleta"),
                    size = 24f
                )
            }
        }.approve(approver)
    }
}