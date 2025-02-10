package io.pdf4k.server.domain

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.pdf4k.server.domain.ServerLens.ServerJackson.auto
import io.pdf4k.server.endpoints.request.PdfRequest
import io.pdf4k.server.endpoints.response.RealmListResponse
import io.pdf4k.server.endpoints.response.StationaryPackListResponse
import io.pdf4k.server.endpoints.response.StationaryPackResponse
import org.http4k.core.Body
import org.http4k.format.ConfigurableJackson
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.Validator
import org.http4k.lens.webForm

object ServerLens {
    object ServerJackson: ConfigurableJackson(ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .setSerializationInclusion(NON_NULL)
    )

    val realmPathLens = Path.of("realm")
    val stationaryPackPathLens = Path.of("stationaryPack")
    val realmListLens = Body.auto<RealmListResponse>().toLens()
    val stationaryPackLens = Body.auto<StationaryPackResponse>().toLens()
    val stationaryPackListLens = Body.auto<StationaryPackListResponse>().toLens()
    val pdfRequestLens = Body.auto<PdfRequest>().toLens()
    val fileNameFieldLens = FormField.map(::FileId, FileId::value).required("name")
    val fileFieldLens = FormField.optional("file")
    val fileFormBodyLens = Body.webForm(Validator.Strict, fileNameFieldLens, fileFieldLens).toLens()
}