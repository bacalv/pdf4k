package io.pdf4k.renderer

import io.pdf4k.domain.PdfPermissions.PdfPermission.entries
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.renderer.InMemoryRenderer.defaultKeyName
import io.pdf4k.testing.AbstractPdfApproverTest
import io.pdf4k.testing.PdfApprover
import io.pdf4k.testing.PdfPassword
import org.junit.jupiter.api.Test

class SecurityTest : AbstractPdfApproverTest() {
    @Test
    fun `render a signed PDF`(approver: PdfApprover) {
        pdf {
            page {
                content { +"A signed PDF" }
            }
            sign(defaultKeyName, "Test signing a PDF", "Bedford Taxi", "01234567890")
        }.approve(approver)
    }

    @Test
    @PdfPassword("userPass")
    fun `password protect a PDF - open as user`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    +"Hello, user"
                }
            }
            encrypt("userPass", "ownerPass", *entries.toTypedArray())
        }.approve(approver)
    }

    @Test
    @PdfPassword("ownerPass")
    fun `password protect a PDF - open as owner`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    +"Hello, owner"
                }
            }
            encrypt("userPass", "ownerPass")
        }.approve(approver)
    }
}