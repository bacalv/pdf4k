package io.pdf4k.testing

import io.pdf4k.domain.Pdf
import io.pdf4k.renderer.PdfError.Companion.PdfErrorException
import io.pdf4k.testing.InMemoryRenderer.renderer
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import java.io.ByteArrayOutputStream

@ExtendWith(PdfApproverExtension::class)
abstract class AbstractPdfRendererTest {
    companion object {
        fun Pdf.approve(approver: PdfApprover) = approver.assertApproved(ByteArrayOutputStream().also { stream ->
            stream.use {
                runCatching { renderer.render(this, it) }.getOrThrow().getOrElse { fail(PdfErrorException(it)) }
            }
        }.toByteArray())
    }
}