package io.pdf4k.example.server

import io.pdf4k.server.testing.AbstractServerTest
import io.pdf4k.testing.PdfApprover
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class MusicianSyncEndpointTest : AbstractServerTest() {
    @Test
    fun `renders musicians`(approver: PdfApprover): Unit = with(emptyScenario()) {
        handler(Request(Method.POST, "/musicians")
            .body("""
                {
                    "musicians": [
                        {
                            "name": "Jimi",
                            "image": "hendrix.png",
                            "bio": "Rather darn good at the old guitar",
                            "dob": [1942, 10, 27],
                            "address": "25 Brook Street, W1K 4HB",
                            "wikiLink": "https://en.wikipedia.org/wiki/Jimi_Hendrix"
                        },
                        {
                            "name": "Rory",
                            "image": "gallagher.jpeg",
                            "bio": "Again, rather darn good at the old guitar",
                            "dob": [1948, 3, 2],
                            "address": "Ballyshannon, County Donegal",
                            "wikiLink": "https://en.wikipedia.org/wiki/Rory_Gallagher"
                        }
                    ]
                }
            """.trimIndent())).let { response ->
                assertAll(
                    { assertEquals(Status.OK, response.status) },
                    { approver.assertApproved(response.body.stream.readAllBytes()) }
                )
        }
    }
}