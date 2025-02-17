package io.pdf4k.plugin.qrcode.acceptance

import io.pdf4k.domain.PdfError.ClasspathResourceNotFound
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.plugin.qrcode.domain.QrStyle
import io.pdf4k.plugin.qrcode.domain.QrStyle.Companion.Logo
import io.pdf4k.plugin.qrcode.domain.QrStyle.Companion.Shape
import io.pdf4k.plugin.qrcode.dsl.qrCodeCell
import io.pdf4k.server.testing.AbstractServerTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import java.awt.Color.*

class QrcodePluginAcceptanceTest : AbstractServerTest() {
    @Test
    fun `render QR code`(approver: PdfApprover) = with(emptyScenario()) {
        operator.rendersAPdfImmediately {
            page {
                content {
                    table(4, noBorder) {
                        Shape.entries.forEach { shape ->
                            qrCodeCell(
                                "https://www.github.com",
                                QrStyle(shape, BLACK, WHITE, 25, logo),
                                width = 40f,
                                height = 40f
                            )
                            qrCodeCell(
                                "https://www.github.com",
                                QrStyle(shape, WHITE, BLACK, 25, logo),
                                width = 40f,
                                height = 40f
                            )
                            qrCodeCell(
                                "https://www.github.com",
                                QrStyle(shape, BLACK, null, 25, null),
                                width = 40f,
                                height = 40f
                            )
                            qrCodeCell(
                                "https://www.github.com",
                                QrStyle(shape, BLUE, null, 25, null),
                                width = 40f,
                                height = 40f
                            )
                        }
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `qr logo not found`(): Unit = with(emptyScenario()) {
        operator.renderingPdfCausesError(ClasspathResourceNotFound("/images/not_found")) {
            page {
                content {
                    table(4, noBorder) {
                        Shape.entries.forEach { shape ->
                            qrCodeCell(
                                "https://www.github.com",
                                QrStyle(shape, BLACK, WHITE, 25, badLogo),
                                width = 40f,
                                height = 40f
                            )
                        }
                    }
                }
            }
        }
    }


    companion object {
        val logo = Logo(local("spades.png"), 300, 300)
        private val badLogo = Logo(local("not_found"), 10, 10)
    }
}