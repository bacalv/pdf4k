package io.pdf4k.client

import io.pdf4k.client.domain.AsyncPdfDtoRequest
import io.pdf4k.client.domain.CallbackMode
import io.pdf4k.client.domain.ClientLens.asyncPdfDtoLens
import io.pdf4k.client.domain.ClientLens.pdfDtoLens
import io.pdf4k.domain.dto.toDto
import io.pdf4k.dsl.PdfBuilder
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.with
import java.net.URL

class Pdf4kServerClient(val handler: HttpHandler) {
    fun renderImmediate(block: PdfBuilder.() -> Unit): Response {
        val request = pdf { block() }.toDto()
        return handler(Request(POST, "/render").with(pdfDtoLens of request))
    }

    fun renderAsync(callbackMode: CallbackMode, callbackUrl: URL, block: PdfBuilder.() -> Unit): Response {
        val request = AsyncPdfDtoRequest(
            callbackMode = callbackMode,
            callbackUrl = callbackUrl,
            payload = pdf { block() }.toDto()
        )
        return handler(Request(POST, "/async/render").with(asyncPdfDtoLens of request))
    }
}