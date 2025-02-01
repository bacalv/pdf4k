package io.pdf4k.approval

import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.Font.Style.Plain
import io.pdf4k.domain.HorizontalAlignment
import io.pdf4k.domain.HorizontalAlignment.*
import io.pdf4k.domain.Stationary
import io.pdf4k.domain.StyleAttributes.Companion.border
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.VerticalAlignment
import io.pdf4k.domain.VerticalAlignment.*
import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.dsl.StationaryBuilder.Companion.plusBlocks
import io.pdf4k.extensions.cartesianProduct
import io.pdf4k.testing.AbstractPdfRendererTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.awt.Color
import java.awt.Color.*

class TableTest : AbstractPdfRendererTest() {
    @Test
    fun `draws a 3x2 table`(approver: PdfApprover) {
        content {
            table(columns = 3) {
                textCell("A1")
                textCell("B1")
                textCell("C1")
                textCell("A2")
                textCell("B2")
                textCell("C2")
            }
        }.approve(approver)
    }

    @Test
    fun `draws a 3x2 table when not all cells fill one row`(approver: PdfApprover) {
        content {
            table(columns = 3) {
                textCell("A1")
                textCell("B1")
                textCell("C1")
                textCell("A2")
                textCell("B2")
            }
        }.approve(approver)
    }

    @Test
    fun `draws table with colSpan and rowSpan`(approver: PdfApprover) {
        content {
            table(columns = 3) {
                textCell(colSpan = 2) { +"A1 and B1" }
                textCell("C1 and C2", rowSpan = 2)
                textCell("A2")
                textCell("B2")
            }
        }.approve(approver)
    }

    @Test
    fun `draws nested table`(approver: PdfApprover) {
        content {
            table(columns = 2) {
                tableCell(columns = 2) {
                    textCell("1A")
                    textCell("1B")
                    textCell("1C")
                    textCell("1D")
                }
                textCell("2")
                textCell("3")
                textCell("4")
            }
        }.approve(approver)
    }

    @ParameterizedTest
    @MethodSource("alignments")
    fun `set cell alignment`(align: HorizontalAlignment, valign: VerticalAlignment, approver: PdfApprover) {
        content {
            table(columns = 2) {
                textCell("Align $align\n$valign")
                style(colour = RED, align = align, valign = valign) {
                    textCell("2")
                    textCell(
                        "The barman of the Horse and Groom didn't deserve this sort of treatment, " +
                                "he was a dignified old man. He pushed his glasses up his nose and blinked at " +
                                "Ford Prefect. Ford ignored him and stared out of the window, so the barman " +
                                "looked instead at Arthur who shrugged helplessly and said nothing."
                    )
                }
                textCell("4")
            }
        }.approve(approver)
    }

    @Test
    fun `set cell background`(approver: PdfApprover) {
        content {
            table(columns = 3) {
                textCell("RED", style(cellBackground = RED))
                textCell("GREEN", style(cellBackground = GREEN))
                textCell("BLUE", style(cellBackground = BLUE))
            }
        }.approve(approver)
    }

    @Test
    fun `set cell padding`(approver: PdfApprover) {
        content {
            style(
                colour = WHITE,
                background = BLUE,
                cellBackground = RED,
                paddingTop = 21f,
                paddingBottom = 40f,
                paddingLeft = 50f,
                paddingRight = 60f
            ) {
                table(columns = 3) {
                    style(align = Center) {
                        textCell("Top", style(valign = Top))
                        textCell("Bottom", style(valign = Bottom))
                    }
                    textCell("LINE 1\nLINE 2")
                    style(valign = Middle) {
                        textCell("Left", style(align = Left))
                        textCell("Right", style(align = Right))
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `set cell borders`(approver: PdfApprover) {
        content {
            table(columns = 1, border(0f)) {
                textCell("TOP RED", style(borderWidthTop = 4f, borderColourTop = RED))
                textCell("BOTTOM GREEN", style(borderWidthBottom = 5f, borderColourBottom = GREEN))
                textCell("LEFT BLUE", style(borderWidthLeft = 6f, borderColourLeft = BLUE))
                textCell("RIGHT YELLOW", style(borderWidthRight = 7f, borderColourRight = YELLOW))
            }
        }.approve(approver)
    }

    @Test
    fun `draw a data table`(approver: PdfApprover) {
        val header = style(
            fontStyle = Bold,
            size = 12f,
            colour = WHITE,
            cellBackground = Color(0x31, 0x3D, 0x5A),
            borderWidthTop = 0.5f,
            borderWidthBottom = 0.5f,
            borderWidthLeft = 0.5f,
            borderWidthRight = 0.5f,
            borderColourTop = WHITE,
            borderColourBottom = WHITE,
            borderColourLeft = WHITE,
            borderColourRight = WHITE,
            align = Center
        )

        val oddRow = header + style(
            fontStyle = Plain,
            align = Left,
            colour = BLACK,
            size = 10f,
            cellBackground = Color(0xEA, 0xEA, 0xEA)
        )

        val evenRow = oddRow + style(cellBackground = Color(0xCB, 0xC5, 0xEA))

        content {
            table(columns = 4, weights = floatArrayOf(2f, 2f, 1f, 3f)) {
                style(header) {
                    textCell("Employee Number")
                    textCell("Name")
                    textCell("DOB")
                    textCell("Address")
                }
                style(oddRow) {
                    textCell("EMP-A3138D")
                    textCell("SMITH, John")
                    textCell("1997-10-30")
                    textCell("123 Fake Street, FA1 K3E")
                }
                style(evenRow) {
                    textCell("EMP-B7727D")
                    textCell("HENDRIX, Jimi")
                    textCell("1942-10-27")
                    textCell("25 Brook Street, W1K 4HB")
                }
                style(oddRow) {
                    textCell("EMP-7UU829")
                    textCell("GILMOUR, David")
                    textCell("1946-03-06")
                    textCell("1 Globe House, N8 8PN")
                }
                style(evenRow) {
                    textCell("EMP-CPB873")
                    textCell("ZAPPA, Frank")
                    textCell("1940-12-21")
                    textCell("2401 Laurel Canyon Blvd, CA 90046")
                }

            }
        }.approve(approver)
    }

    @Test
    fun `draws a table with reduced width`(approver: PdfApprover) {
        content {
            table(2, widthPercentage = 50f) {
                textCell("Col 1")
                textCell("Cell 2")
            }

        }.approve(approver)
    }

    @Test
    fun `extends table to fill the rest of the block`(approver: PdfApprover) {
        val stationary = Stationary.BlankA4Portrait.plusBlocks {
            block("normal", 0f, 0f, 250f, 642.88f)
            block("extended", 260f, 0f, 250f, 642.88f)
        }

        pdf {
            page(stationary = stationary) {
                listOf("extended" to true, "normal" to false).forEach { (blockName, extend) ->
                    block(blockName) {
                        style(cellBackground = RED, colour = WHITE) {
                            table(1, extend = extend) {
                                textCell("extend=$extend")
                            }
                        }
                    }
                }
            }
        }.approve(approver)
    }

    companion object {
        @JvmStatic
        fun alignments() = HorizontalAlignment.entries.cartesianProduct(VerticalAlignment.entries)
    }
}