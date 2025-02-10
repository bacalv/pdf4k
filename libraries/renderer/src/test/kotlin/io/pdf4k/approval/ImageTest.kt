package io.pdf4k.approval

import io.pdf4k.domain.Font
import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.HorizontalAlignment.*
import io.pdf4k.domain.Leading.Companion.multiplier
import io.pdf4k.domain.Margin
import io.pdf4k.domain.QrStyle
import io.pdf4k.domain.QrStyle.Companion.Logo
import io.pdf4k.domain.QrStyle.Companion.Shape
import io.pdf4k.domain.QrStyle.Companion.Shape.Square
import io.pdf4k.domain.ResourceLocation.Companion.custom
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.StyleAttributes.Companion.border
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.VerticalAlignment.Middle
import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.testing.AbstractPdfRendererTest
import io.pdf4k.testing.PdfApprover
import io.pdf4k.testing.RemoteServer
import io.pdf4k.testing.domain.Musician.Companion.musicians
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.Color.*
import java.net.URI

class ImageTest : AbstractPdfRendererTest() {
    @Test
    fun `renders images`(approver: PdfApprover) {
        content {
            style(splitLate = true, splitRows = true) {
                table(columns = 2, weights = listOf(2f, 3f), headerRows = 1) {
                    style(header) {
                        textCell("Musician")
                        textCell("Details")
                    }
                    musicians.forEachIndexed { index, artist ->
                        style(if (index % 2 == 0) evenRow else oddRow) {
                            imageCell(
                                resource = "musicians/${artist.image}",
                                style = style(align = Center, valign = Middle),
                                width = 200f,
                                height = 200f
                            )
                            tableCell(1, noBorder) {
                                tableCell(2) {
                                    textCell(nameStyle) { +artist.name }
                                    qrCodeCell(
                                        link = artist.wikiLink,
                                        qrStyle = QrStyle(
                                            shape = Square,
                                            colour = BLACK,
                                            background = if (index % 2 == 0) evenRowColour else oddRowColour,
                                            size = 50,
                                            logo = logo
                                        ),
                                        style = style(align = Right)
                                    )
                                }
                                textCell(bioStyle) { +artist.bio }
                                tableCell(columns = 1, margin = Margin(0f, 0f, 24f, 24f)) {
                                    tableCell(2, bioTable, weights = listOf(1f, 3f)) {
                                        textCell("DOB", bioHeader + style(borderColourBottom = WHITE))
                                        textCell(artist.dob.toString())
                                        textCell("Address", bioHeader)
                                        textCell(artist.address)
                                    }
                                }
                                textCell(" ")
                            }
                        }
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `rotate image`(approver: PdfApprover) {
        content {
            table(2) {
                listOf(45f, 135f, 225f, 315f).forEach { it ->
                    imageCell("musicians/gallagher.jpeg", width = 200f, height = 200f, rotation = it)
                }
            }

        }.approve(approver)
    }

    @Test
    fun `load image from url`(approver: PdfApprover) {
        RemoteServer().let { server ->
            val port = server.start()
            content {
                table(style = noBorder) {
                    imageCell(
                        URI("http://localhost:$port/ELO_A_New_World_Record.jpg"),
                        width = 200f,
                        height = 200f
                    )
                }

            }.approve(approver)
            server.stop()
        }
    }

    @Test
    fun `load image from custom provider`(approver: PdfApprover) {
        content {
            table(style = noBorder) {
                imageCell(
                    custom("custom", "dsotm.jpg"),
                    width = 200f,
                    height = 200f
                )
            }

        }.approve(approver)
    }

    @Test
    fun `renders image in a paragraph`(approver: PdfApprover) {
        content {
            table(1, noBorder + style(cellBackground = BLACK, colour = WHITE)) {
                textCell("Here's some text on the line above")
                textCell {
                    +"Here's a bomb >>> "
                    image("bomb.jpeg")
                    +" <<< see?"
                }
                textCell("Here's some text on the line below")
            }
            paragraph {
                image("adams.png")
            }
        }.approve(approver)
    }

    @Test
    fun `render QR code`(approver: PdfApprover) {
        content {
            table(4, noBorder) {
                Shape.entries.forEach { shape ->
                    qrCodeCell("https://www.github.com", QrStyle(shape, BLACK, WHITE, 25, logo))
                    qrCodeCell("https://www.github.com", QrStyle(shape, WHITE, BLACK, 25, logo))
                    qrCodeCell("https://www.github.com", QrStyle(shape, BLACK, null, 25, null))
                    qrCodeCell("https://www.github.com", QrStyle(shape, BLUE, null, 25, null))
                }
            }

        }.approve(approver)
    }

    @Test
    fun `image scaling`(approver: PdfApprover) {
        val resource = "adams.png"
        content {
            table(2, weights = listOf(1f, 9f)) {
                textCell("Both set")
                imageCell(resource, width = 50f, height = 50f)
                textCell("Height")
                imageCell(resource, width = null, height = 50f)
                textCell("Width")
                imageCell(resource, width = 50f, height = null)
                textCell("Neither")
                imageCell(resource)
            }
        }.approve(approver)
    }

    companion object {
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
            align = Center,
            paddingTop = 4f,
            paddingBottom = 8f,
            paddingLeft = 8f,
            paddingRight = 8f
        )
        val oddRowColour = Color(0xEA, 0xEA, 0xEA)
        val oddRow = header + style(
            fontStyle = Font.Style.Plain,
            align = Left,
            colour = BLACK,
            size = 10f,
            cellBackground = oddRowColour
        )
        val evenRowColour = Color(0xCB, 0xC5, 0xEA)
        val evenRow = oddRow + style(cellBackground = evenRowColour)
        val nameStyle = style(fontStyle = Bold, size = 14f)
        val bioStyle = style(align = Justified, leading = multiplier(2f), paddingBottom = 24f)
        val bioTable = border(1f, Color(0x31, 0x3D, 0x5A)) + style(cellBackground = WHITE, size = 10f)
        val bioHeader = style(
            fontStyle = Bold,
            size = 10f,
            colour = WHITE,
            cellBackground = Color(0x31, 0x3D, 0x5A)
        )
        val logo = Logo(local("spades.png"), 300, 300)
    }
}