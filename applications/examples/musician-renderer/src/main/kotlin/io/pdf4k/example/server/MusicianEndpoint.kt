package io.pdf4k.example.server

import io.pdf4k.domain.Font
import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.HorizontalAlignment.*
import io.pdf4k.domain.Leading.Companion.multiplier
import io.pdf4k.domain.Margin
import io.pdf4k.domain.Pdf
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.StyleAttributes.Companion.border
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.VerticalAlignment.Middle
import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.example.server.MusicianEndpoint.MusicianList
import io.pdf4k.plugin.qrcode.domain.QrStyle
import io.pdf4k.plugin.qrcode.domain.QrStyle.Companion.Logo
import io.pdf4k.plugin.qrcode.domain.QrStyle.Companion.Shape
import io.pdf4k.plugin.qrcode.dsl.qrCodeCell
import io.pdf4k.server.domain.ServerLens.ServerJackson.auto
import io.pdf4k.server.endpoints.SyncPdfEndpoint
import io.pdf4k.testing.domain.Musician
import io.pdf4k.testing.domain.Musician.Companion.musicians
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens
import java.awt.Color
import java.awt.Color.BLACK
import java.awt.Color.WHITE

@Suppress("unused")
class MusicianEndpoint : SyncPdfEndpoint<MusicianList> {
    data class MusicianList(
        val musicians: List<Musician>
    )

    override val path: String = "musicians"
    override val summary: String = "Render musicians"
    override val requestLens: BiDiBodyLens<MusicianList> = Body.auto<MusicianList>().toLens()
    override val example = MusicianList(musicians)
    override val pdfFn: (MusicianList) -> Pdf = { list ->
        content {
            style(splitLate = true, splitRows = true) {
                table(columns = 2, weights = listOf(2f, 3f), headerRows = 1) {
                    style(header) {
                        textCell("Musician")
                        textCell("Details")
                    }
                    list.musicians.forEachIndexed { index, artist ->
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
                                            shape = Shape.Square,
                                            colour = BLACK,
                                            background = if (index % 2 == 0) evenRowColour else oddRowColour,
                                            size = 50,
                                            logo = logo
                                        ),
                                        style = style(align = Right),
                                        width = 50f,
                                        height = 50f
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
        }
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
            paddingTop = 8f,
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