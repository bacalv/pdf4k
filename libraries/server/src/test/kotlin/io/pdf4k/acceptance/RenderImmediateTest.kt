package io.pdf4k.acceptance

import io.pdf4k.domain.Font
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.server.testing.AbstractServerTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test

class RenderImmediateTest : AbstractServerTest() {
    @Test
    fun `renders a PDF synchronously`(approver: PdfApprover): Unit = with(emptyScenario()) {
        val stationary = BlankA4Portrait.copy(template = local("Example"))
        val font = Font.Resource(local("CookieCrisp.ttf"), "CookieCrisp")

        operator.rendersAPdfImmediately {
            section(style(font = font), stationary = stationary) {
                content {
                    +"Here is an image..."
                    table {
                        imageCell(local("img.png"), width = 200f, height = 200f)
                    }
                }
            }
        }.approve(approver)
    }
}