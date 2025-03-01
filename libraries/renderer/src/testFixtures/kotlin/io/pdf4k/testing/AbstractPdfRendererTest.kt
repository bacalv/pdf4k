package io.pdf4k.testing

import io.pdf4k.domain.Pdf
import io.pdf4k.domain.dto.toDomain
import io.pdf4k.domain.dto.toDto
import io.pdf4k.testing.InMemoryRenderer.renderer
import org.junit.jupiter.api.extension.ExtendWith
import java.io.ByteArrayOutputStream

@ExtendWith(PdfApproverExtension::class)
abstract class AbstractPdfRendererTest {
    companion object {
        fun Pdf.approve(approver: PdfApprover) = approver.assertApproved(ByteArrayOutputStream().also { stream ->
            stream.use {
                val dto = this.toDto()
                renderer.render(dto.toDomain().first, it)
            }
        }.toByteArray())
    }
}