package io.pdf4k.approval

import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.testing.AbstractPdfRendererTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test

class MetadataTest : AbstractPdfRendererTest() {
    @Test
    fun `adds metadata`(approver: PdfApprover) {
        pdf {
            page {
                content { +"Some PDF with metadata" }
            }
            metadata {
                title = "Hitchhiker's Guide to the Galaxy"
                author = "Douglas Adams"
                subject = "Science Fiction"
                keywords = "42"
                creator = "io.pdf4k"
                producer = "io.pdf4k"

                customProperty("CUSTOM_KEY", "CUSTOM_KEY Value")
                customProperty("ANOTHER_CUSTOM_KEY", "ANOTHER_CUSTOM_KEY Value")
            }
        }.approve(approver)
    }
}