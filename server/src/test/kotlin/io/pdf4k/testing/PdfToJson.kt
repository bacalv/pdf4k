package io.pdf4k.testing

import io.pdf4k.domain.*
import io.pdf4k.domain.Component.*
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.dto.toDto
import io.pdf4k.json.domainDtoObjectMapper
import io.pdf4k.server.endpoints.request.PdfRequest
import java.time.ZoneId
import java.time.ZonedDateTime

fun main() {
    val examplePage = Page(
        listOf(Stationary.BlankA4Portrait),
        style = null,
        content = Content(listOf(
            Paragraph(listOf(
                Phrase(listOf(Chunk("Hello World!")))
            ))
        )),
        blockContent = emptyMap()
    )
    val examplePdfRequest = PdfRequest(
        Pdf(
            style(size = 16f),
            listOf(examplePage),
            PdfMetadata.empty,
            Signature(
                KeyName("KEY"),
                "reason",
                "location",
                "contact",
                ZonedDateTime.now(ZoneId.systemDefault())
            ),
            PdfPermissions("user", "owner", emptySet())
        ).toDto()
    )
    println(domainDtoObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(examplePdfRequest))
}