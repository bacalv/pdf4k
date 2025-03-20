package io.pdf4k.client.domain

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.pdf4k.client.domain.ClientLens.ClientJackson.auto
import io.pdf4k.domain.dto.PdfDto
import org.http4k.core.Body
import org.http4k.format.ConfigurableJackson

object ClientLens {
    @Suppress("unused")
    object ClientJackson: ConfigurableJackson(ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .setSerializationInclusion(NON_NULL)
    )

    val pdfDtoLens = Body.auto<PdfDto>().toLens()
    val asyncPdfDtoLens = Body.auto<AsyncPdfDtoRequest>().toLens()
    val asyncPdfResponseLens = Body.auto<AsyncPdfResponse>().toLens()
}