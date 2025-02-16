package io.pdf4k.client

import io.pdf4k.client.domain.ClientLens.pdfDtoLens
import io.pdf4k.domain.dto.toDto
import io.pdf4k.dsl.PdfBuilder
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.with

class Pdf4kServerClient(val handler: HttpHandler) {
    fun renderImmediate(block: PdfBuilder.() -> Unit): Response {
        val request = pdf { block() }.toDto()
        return handler(Request(POST, "/render").with(pdfDtoLens of request))
    }
}