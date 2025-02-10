package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfWriter
import io.mockk.mockk
import io.pdf4k.domain.Stationary
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream

class RendererContextTest {
    @Test
    fun `throws exception if stationary not found`() {
        val context = RendererContext(
            Document(),
            PdfWriter.getInstance(Document(), ByteArrayOutputStream()),
            Document(),
            PdfWriter.getInstance(Document(), ByteArrayOutputStream()),
            emptyMap(),
            mockk()
        )
        assertThrows<IllegalStateException> { context.nextPage(Stationary.BlankA4Portrait, 1) }
    }
}