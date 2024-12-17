package io.pdf4k.renderer

import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.testing.AbstractPdfApproverTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import pro.juxt.pdf4k.testing.PdfApprover

@Disabled
class SigningTest : AbstractPdfApproverTest() {
    @Test
    fun `render a signed PDF`(approver: PdfApprover) {
        pdf {
            page {
                content { +"A signed PDF" }
            }
        }.approve(approver)
    }
}