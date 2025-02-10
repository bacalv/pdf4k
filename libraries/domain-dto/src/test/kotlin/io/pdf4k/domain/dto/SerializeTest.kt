package io.pdf4k.domain.dto

import com.oneeyedmen.okeydoke.Approver
import com.oneeyedmen.okeydoke.internal.DirectoryFinder
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import io.pdf4k.domain.*
import io.pdf4k.domain.Font.Resource
import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.HorizontalAlignment.*
import io.pdf4k.domain.Leading.Companion.multiplier
import io.pdf4k.domain.PdfPermissions.PdfPermission
import io.pdf4k.domain.QrStyle.Companion.Logo
import io.pdf4k.domain.QrStyle.Companion.Shape.Square
import io.pdf4k.domain.ResourceLocation.Companion.custom
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.StyleAttributes.Companion.border
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.VerticalAlignment.Middle
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.json.toObject
import io.pdf4k.json.toPrettyJsonString
import io.pdf4k.testing.domain.Musician.Companion.musicians
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.RegisterExtension
import java.awt.Color
import java.awt.Color.*
import java.io.File
import java.time.LocalDate
import java.time.ZoneId

class SerializeTest {
    @RegisterExtension
    var approvals: ApprovalsExtension = ApprovalsExtension(DirectoryFinder.firstDirThatExists(File("src/test/resources")))

    @Test
    fun `convert pdf to json`(approver: Approver) {
        pdf {
            encrypt("userPass", "ownerPass", *PdfPermission.entries.toTypedArray())
            sign(
                keyName = KeyName("key-name"),
                reason = "Test signing a PDF",
                location = "Bedford Taxi",
                contact = "01234567890",
                signDate = LocalDate.of(2012, 12, 23).atStartOfDay(ZoneId.systemDefault()))

            metadata {
                title = "Hitchhiker's Guide to the Galaxy"
                author = "Douglas Adams"
                subject = "Science Fiction"
                keywords = "42"
                creator = "io.pdf4k"
                producer = "io.pdf4k"

                customProperty("CUSTOM_KEY", "CUSTOM_KEY Value")
                customProperty("ANOTHER_CUSTOM_KEY", "ANOTHER_CUSTOM_KEY Value")
            }
            page {
                content {
                    paragraph(style()) {
                        +"Here is a paragraph with an empty style "
                        link("#link", "Link", style(colour = BLUE))
                    }
                }
            }
            page {
                content {
                    paragraph {
                        anchor("link", "Here's an anchor")
                    }
                }
            }
            page(style(font, colour = RED)) {
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
                                                textCell(
                                                    "DOB",
                                                    bioHeader + style(borderColourBottom = WHITE)
                                                )
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
        }.approve(approver)

    }

    companion object {
        fun Pdf.approve(approver: Approver) {
            val dto = toDto()
            val json = dto.toPrettyJsonString()
            val deserialized = json.toObject<PdfDto>()
            assertAll(
                { assertEquals(dto, deserialized, "Deserialized matches") },
                { approver.assertApproved(json) }
            )
        }

        val font = Resource(custom("custom", "ShadeBlue-2OozX.ttf"), "Shade Blue")
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