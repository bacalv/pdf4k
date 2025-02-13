package io.pdf4k.server.domain

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.pdf4k.server.domain.ServerLens.ServerJackson.auto
import io.pdf4k.server.endpoints.request.PdfRequest
import org.http4k.core.Body
import org.http4k.format.ConfigurableJackson
import org.http4k.lens.FormField

object ServerLens {
    object ServerJackson: ConfigurableJackson(ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .setSerializationInclusion(NON_NULL)
    )

    val pdfRequestLens = Body.auto<PdfRequest>().toLens()
    val fileNameFieldLens = FormField.map(::FileId, FileId::value).required("name")
    val fileFieldLens = FormField.optional("file")
}