package io.pdf4k.acceptance

import io.pdf4k.client.domain.CallbackMode
import io.pdf4k.domain.Font
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.server.testing.AbstractServerTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import java.net.URI
import java.util.*

class RenderAsyncTest : AbstractServerTest() {
    @Test
    fun `renders a PDF asynchronously`(approver: PdfApprover): Unit = with(emptyScenario()) {
        val stationary = BlankA4Portrait.copy(template = local("Example"))
        val font = Font.Resource(local("CookieCrisp.ttf"), "CookieCrisp")

        val jobRef = UUID.randomUUID()
        val (port, server) = operator.startsCallbackServer()

        operator.rendersAPdfAsynchronously(
            callbackMode = CallbackMode.POST_RESULT,
            callbackUrl = URI.create("http://localhost:$port/pdf-callback/$jobRef").toURL()
        ) {
            page(style(font = font), stationary = stationary) {
                content {
                    +"Here is an image..."
                    table {
                        imageCell(local("img.png"), width = 200f, height = 200f)
                    }
                }
            }
        }

        operator.waitsForCallback(jobRef, server).approve(approver)

        server.stop()
    }
}