package io.pdf4k.client.domain

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.pdf4k.client.domain.ClientLens.ClientJackson.auto
import org.http4k.core.Body
import org.http4k.format.ConfigurableJackson
import org.http4k.lens.MultipartFormField
import org.http4k.lens.MultipartFormFile
import org.http4k.lens.Validator
import org.http4k.lens.multipartForm

object ClientLens {
    object ClientJackson: ConfigurableJackson(ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .setSerializationInclusion(NON_NULL)
    )

    val realmListLens = Body.auto<RealmList>().toLens()
    val stationaryPackLens = Body.auto<StationaryPack>().toLens()
    val stationaryPacksListLens = Body.auto<StationaryPackList>().toLens()
    val pdfRequestLens = Body.auto<PdfRequest>().toLens()
    val fileNameFieldLens = MultipartFormField.string().map(::FileId, FileId::value).required("name")
    val fileFieldLens = MultipartFormFile.optional("file")
    val fileFormBodyLens = Body.multipartForm(Validator.Strict, fileNameFieldLens, fileFieldLens, diskThreshold = 5).toLens()
}