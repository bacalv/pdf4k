package io.pdf4k.server.scenario

import io.pdf4k.client.Pdf4kServerClient
import io.pdf4k.client.domain.CallbackMode
import io.pdf4k.client.domain.ClientLens.asyncPdfResponseLens
import io.pdf4k.domain.PdfError
import io.pdf4k.dsl.PdfBuilder
import org.awaitility.kotlin.await
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.Duration
import java.util.*

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

    fun rendersAPdfAsynchronously(
        callbackMode: CallbackMode,
        callbackUrl: URL,
        block: PdfBuilder.() -> Unit
    ): UUID {
        val response = client.renderAsync(callbackMode, callbackUrl, block)
        assertEquals(ACCEPTED, response.status)
        return asyncPdfResponseLens(response).jobId
    }

    fun startsCallbackServer(): Pair<Int, CallbackServer> {
        val server = CallbackServer()
        val port = server.start()
        return port to server
    }

    fun waitsForCallback(jobRef: UUID, server: CallbackServer): ByteArray {
        await.atMost(Duration.ofSeconds(5)).until { server[jobRef] != null }
        return server[jobRef] ?: fail()
    }
}