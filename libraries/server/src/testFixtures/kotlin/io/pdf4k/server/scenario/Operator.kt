package io.pdf4k.server.scenario

import io.pdf4k.client.Pdf4kServerClient
import io.pdf4k.domain.PdfError
import io.pdf4k.dsl.PdfBuilder
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.ByteArrayOutputStream

class Operator(private val client: Pdf4kServerClient) {
    fun viewsOpenAPIDocs(): String {
        val response = client.handler(Request(GET, "/api/openapi.json"))
        assertEquals(OK, response.status)
        return response.bodyString()
    }

    fun rendersAPdfImmediately(
        block: PdfBuilder.() -> Unit
    ): ByteArray {
        val response = client.renderImmediate(block)
        assertEquals(OK, response.status)
        val result = ByteArrayOutputStream()
        response.body.stream.copyTo(result)
        return result.toByteArray()
    }

    fun renderingPdfCausesError(
        expectedError: PdfError,
        expectedStatus: Status = NOT_FOUND,
        block: PdfBuilder.() -> Unit
    ) {
        val response = client.renderImmediate(block)
        assertEquals(expectedStatus, response.status)
        assertEquals(expectedError.toString(), response.bodyString())
    }
}