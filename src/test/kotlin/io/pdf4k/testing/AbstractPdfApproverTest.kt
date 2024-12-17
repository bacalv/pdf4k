package io.pdf4k.testing

import io.pdf4k.domain.Pdf
import io.pdf4k.renderer.PdfRenderer.render
import org.junit.jupiter.api.extension.ExtendWith
import pro.juxt.pdf4k.testing.PdfApprover
import pro.juxt.pdf4k.testing.PdfApproverExtension
import java.io.ByteArrayOutputStream

@ExtendWith(PdfApproverExtension::class)
abstract class AbstractPdfApproverTest {

    companion object {
        fun Pdf.approve(approver: PdfApprover) = approver.assertApproved(ByteArrayOutputStream().also { stream ->
            stream.use { render(it) }
        }.toByteArray())
    }
}