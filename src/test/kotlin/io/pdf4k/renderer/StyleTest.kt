package io.pdf4k.renderer

import io.pdf4k.domain.Font.BuiltIn.Ariel
import io.pdf4k.domain.Font.Style.*
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.testing.AbstractPdfApproverTest
import org.junit.jupiter.api.Test
import io.pdf4k.testing.PdfApprover
import java.awt.Color.*

class StyleTest : AbstractPdfApproverTest() {
    @Test
    fun `renders different colour text`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    phrase(style(font = Ariel, fontStyle = Bold, size = 16f)) {
                        "RED" and style(colour = RED)
                        " GREEN" and style(colour = GREEN)
                        " BLUE" and style(colour = BLUE)
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders different colour text backgrounds`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    phrase(style(font = Ariel, fontStyle = Bold, size = 16f, colour = WHITE)) {
                        "RED" and style(background = RED)
                        +" "
                        "GREEN" and style(background = GREEN)
                        +" "
                        "BLUE" and style(background = BLUE)
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders underlined text`(approver: PdfApprover) {
        pdf {
            page {
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
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders different font styles`(approver: PdfApprover) {
        pdf {
            page {
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
                }
            }
        }.approve(approver)
    }

    @Test
    fun `applies styles to elements at different levels`(approver: PdfApprover) {
        pdf {
            page {
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
}