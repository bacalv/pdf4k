package io.pdf4k.server.endpoints

import io.pdf4k.domain.dto.*
import io.pdf4k.domain.dto.ComponentDto.*
import io.pdf4k.domain.dto.PdfPermissionsDto.PdfPermission.Assembly
import io.pdf4k.domain.dto.ResourceLocationDto.Local
import io.pdf4k.server.domain.ServerLens.pdfRequestLens
import io.pdf4k.server.domain.StationaryPack.Companion.emptyStationaryPack
import io.pdf4k.server.endpoints.request.PdfRequest
import io.pdf4k.server.service.Pdf4kServices
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import java.awt.Color
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

object RenderRoutes {
    fun routes(services: Pdf4kServices) = listOf(
        "/render" meta {
            summary = "Renders a PDF."
            receiving(pdfRequestLens)// to examplePdfRequest)
            returning(OK)
        } bindContract POST to { request ->
            val (pdf, resourceMap) = pdfRequestLens(request).pdf.toDomain()
            runCatching {
                services.renderingService.render(emptyStationaryPack, pdf, resourceMap)
            }.map { inputStream ->
                Response(OK)
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"${UUID.randomUUID()}.pdf\"")
                    .body(inputStream)
            }.getOrElse { ErrorHandler(it) }
        }
    )

    // TODO - doesn't work
    private val examplePdfRequest = PdfRequest(
        pdf = PdfDto(
            resourceMap = ResourceMapDto(
                colours = listOf(
                    Color.WHITE.toDto(),
                    Color.RED.toDto(),
                    Color.BLUE.toDto()
                ),
                fonts = listOf(FontDto.Resource(1, "arial unicode ms", "ttf")),
                styles = listOf(
                    StyleAttributesDto(
                        font = 1,
                        fontStyle = FontDto.Style.Bold,
                        size = 16f,
                        colour = 0,
                        background = 1,
                        underlined = true,
                        underlineColour = 2,
                        leading = LeadingDto(0f, 1.5f),
                        align = HorizontalAlignmentDto.Center,
                        valign = VerticalAlignmentDto.Middle,
                        cellBackground = 1,
                        paddingTop = 4f,
                        paddingBottom = 4f,
                        paddingLeft = 4f,
                        paddingRight = 4f,
                        borderWidthTop = 1f,
                        borderWidthBottom = 1f,
                        borderWidthLeft = 1f,
                        borderWidthRight = 1f,
                        borderColourTop = 2,
                        borderColourBottom = 2,
                        borderColourLeft = 2,
                        borderColourRight = 2,
                        splitLate = true,
                        splitRows = true
                    )
                ),
                stationary = listOf(
                    StationaryDto(
                        template = 0,
                        templatePage = 1,
                        width = 595.92f,
                        height = 842.88f,
                        blocks = mapOf(
                            "main" to BlockDto(36f, 36f, 523.92f, 770.88f),
                            "footer" to BlockDto(36f, 0f, 523.92f, 36f),
                        ),
                        contentFlow = listOf("main")
                    )
                ),
                resourceLocations = listOf(
                    Local("blank-a4-portrait"),
                    Local("arial-unicode-ms.ttf")
                )
            ),
            style = 0,
            pages = listOf(
                PageDto(
                    stationary = listOf(0),
                    style = 0,
                    content = listOf(
                        Paragraph(
                            children = listOf(
                                Phrase(
                                    children = listOf(
                                        Chunk("Hello World")
                                    )
                                )
                            )
                        )
                    ),
                    blockContent = mapOf(
                        "footer" to listOf(
                            Paragraph(
                                children = listOf(
                                    Phrase(children = listOf(PageNumber))
                                )
                            )
                        )
                    )
                )
            ),
            metadata = PdfMetadataDto(
                title = "Hello World",
                author = "io.pdf4k",
                subject = "Hello World PDF",
                keywords = "hello world",
                creator = "io.pdf4k",
                producer = "io.pdf4k",
                customProperties = mapOf("custom-property" to "value")
            ),
            signature = SignatureDto(
                keyName = "key",
                reason = "To say hello",
                location = "In my computer",
                contact = "io.pdf4k",
                signDateUtc = LocalDate.of(1978, 12, 23).atStartOfDay().toInstant(ZoneOffset.UTC)
            ),
            permissions = PdfPermissionsDto(
                userPassword = "user",
                ownerPassword = "owner",
                permissions = setOf(Assembly)
            )
        )
    )
}