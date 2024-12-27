package io.pdf4k.renderer

import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.testing.AbstractPdfApproverTest
import org.junit.jupiter.api.Test
import pro.juxt.pdf4k.testing.PdfApprover

class MetadataTest : AbstractPdfApproverTest() {
    @Test
    fun `adds a link to another part of the document`(approver: PdfApprover) {
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
            }
        }.approve(approver)
    }
}