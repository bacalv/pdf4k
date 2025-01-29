package io.pdf4k.testing

import io.pdf4k.approval.InMemoryRenderer.renderer
import io.pdf4k.domain.Pdf
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayOutputStream

@ExtendWith(PdfApproverExtension::class)
abstract class AbstractPdfApproverTest {
    companion object {
        fun Pdf.approve(approver: PdfApprover) = approver.assertApproved(ByteArrayOutputStream().also { stream ->
            stream.use { renderer.render(this, it) }
        }.toByteArray())
    }
}