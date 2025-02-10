package io.pdf4k.acceptance

import io.pdf4k.domain.Font
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.testing.AbstractServerTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test

class RenderImmediateTest : AbstractServerTest() {
    @Test
    fun `renders a PDF synchronously`(approver: PdfApprover): Unit = with(emptyScenario()) {
        operator.createsRealm(realmName)
        operator.createsStationaryPack(realmName, stationaryPackName)
        operator.uploadsPageTemplate(realmName, stationaryPackName, "example.pdf", "Example")
        operator.uploadsFont(realmName, stationaryPackName, "CookieCrisp-L36ly.ttf", "CookieCrisp")
        operator.uploadsImage(realmName, stationaryPackName, "musicians/hendrix.png", "Hendrix")

        val stationary = BlankA4Portrait.copy(template = local("Example"))
        val font = Font.Resource(local("CookieCrisp"), "CookieCrisp")

        operator.rendersAPdfImmediately(realmName, stationaryPackName) {
            page(style(font = font), stationary = stationary) {
                content {
                    +"Here is an image..."
                    table {
                        imageCell(local("Hendrix"), width = 200f, height = 200f)
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders a PDF synchronously in default realm`(approver: PdfApprover): Unit = with(emptyScenario()) {
        operator.rendersAPdfImmediately {
            page {
                content {
                    +"Hello world"
                }
            }
        }.approve(approver)
    }
}