package io.pdf4k.example.server

import io.pdf4k.example.domain.MusicianAsyncRequest
import io.pdf4k.example.domain.MusicianList
import io.pdf4k.server.domain.CallbackMode.POST_RESULT
import io.pdf4k.server.domain.ServerLens.ServerJackson.auto
import io.pdf4k.server.testing.AbstractServerTest
import io.pdf4k.testing.PdfApprover
import io.pdf4k.testing.domain.Musician.Companion.musicians
import org.http4k.core.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URI
import java.util.*

class MusicianAsyncEndpointTest : AbstractServerTest() {
    @Test
    fun `renders musicians`(approver: PdfApprover): Unit = with(emptyScenario()) {
        val jobRef = UUID.randomUUID()
        val (port, server) = operator.startsCallbackServer()

        handler(Request(Method.POST, "/async")
            .with(requestLens of MusicianAsyncRequest(
                callbackMode = POST_RESULT,
                callbackUrl = URI.create("http://localhost:$port/pdf-callback/$jobRef").toURL(),
                payload = MusicianList(listOf(musicians.first()))
            ))
        ).let { response -> assertEquals(Status.ACCEPTED, response.status) }

        operator.waitsForCallback(jobRef, server).approve(approver)

        server.stop()
    }

    companion object {
        private val requestLens =  Body.auto<MusicianAsyncRequest>().toLens()
    }
}