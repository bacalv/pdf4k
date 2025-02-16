package io.pdf4k.server.testing

import io.pdf4k.domain.*
import io.pdf4k.domain.Component.*
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.dto.toDto
import io.pdf4k.json.domainDtoObjectMapper
import java.time.ZoneId
import java.time.ZonedDateTime

fun main() {
    val examplePage = Page(
        listOf(Stationary.BlankA4Portrait),
        style = null,
        content = Content(
            listOf(
                Paragraph(
                    listOf(
                        Phrase(listOf(Chunk("Some Nice Message")))
                    )
                )
            )
        ),
        blockContent = emptyMap()
    )
    val examplePdfRequest = Pdf(
        style(size = 16f),
        listOf(examplePage),
        PdfMetadata.empty,
        Signature(
            KeyName("test-key"),
            "reason",
            "location",
            "contact",
            ZonedDateTime.now(ZoneId.systemDefault())
        ),
        permissions = PdfPermissions("user", "owner", PdfPermissions.PdfPermission.entries.toSet())
    ).toDto()

    println(domainDtoObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(examplePdfRequest))
}