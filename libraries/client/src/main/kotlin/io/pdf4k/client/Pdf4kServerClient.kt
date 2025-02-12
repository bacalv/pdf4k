package io.pdf4k.client

import io.pdf4k.client.domain.ClientLens.fileFieldLens
import io.pdf4k.client.domain.ClientLens.fileFormBodyLens
import io.pdf4k.client.domain.ClientLens.fileNameFieldLens
import io.pdf4k.client.domain.ClientLens.pdfRequestLens
import io.pdf4k.client.domain.FileId
import io.pdf4k.client.domain.PdfRequest
import io.pdf4k.domain.dto.toDto
import io.pdf4k.dsl.PdfBuilder
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.lens.MultipartForm
import org.http4k.lens.MultipartFormFile
import java.io.InputStream

class Pdf4kServerClient(val handler: HttpHandler) {
    fun createRealm(realmName: String) = handler(Request(PUT, "/realms/$realmName"))

    fun listRealms() = handler(Request(GET, "/realms/"))

    fun createStationaryPack(realmName: String, stationaryPackName: String) =
        handler(Request(PUT, "/realms/$realmName/$stationaryPackName"))

    fun findStationaryPack(realmName: String, stationaryPackName: String) =
        handler(Request(GET, "/realms/$realmName/$stationaryPackName"))

    fun listsStationaryPacksForRealm(realmName: String) = handler(Request(GET, "/realms/$realmName"))

    fun uploadPageTemplate(
        realmName: String,
        stationaryPackName: String,
        inputStream: InputStream,
        pageTemplateName: String
    ): Response {
        val multipartForm = MultipartForm().with(
            fileNameFieldLens of FileId(pageTemplateName),
            fileFieldLens of MultipartFormFile(
                pageTemplateName,
                ContentType.OCTET_STREAM,
                inputStream
            )
        )
        return handler(Request(POST, "/realms/$realmName/$stationaryPackName/page-template")
            .with(fileFormBodyLens of multipartForm))
    }

    fun uploadFont(
        realmName: String,
        stationaryPackName: String,
        inputStream: InputStream,
        fontName: String
    ): Response {
        val multipartForm = MultipartForm().with(
            fileNameFieldLens of FileId(fontName),
            fileFieldLens of MultipartFormFile(
                fontName,
                ContentType.OCTET_STREAM,
                inputStream
            )
        )
        return handler(Request(POST, "/realms/$realmName/$stationaryPackName/font")
            .with(fileFormBodyLens of multipartForm))
    }

    fun uploadImage(
        realmName: String,
        stationaryPackName: String,
        inputStream: InputStream,
        imageName: String
    ): Response {
        val multipartForm = MultipartForm().with(
            fileNameFieldLens of FileId(imageName),
            fileFieldLens of MultipartFormFile(
                imageName,
                ContentType.OCTET_STREAM,
                inputStream
            )
        )
        return handler(Request(POST, "/realms/$realmName/$stationaryPackName/image")
            .with(fileFormBodyLens of multipartForm))
    }

    fun renderImmediate(realmName: String, stationaryPackName: String, block: PdfBuilder.() -> Unit): Response {
        val request = PdfRequest(pdf { block() }.toDto())
        return handler(Request(POST, "/realms/$realmName/$stationaryPackName/render").with(pdfRequestLens of request))
    }
}