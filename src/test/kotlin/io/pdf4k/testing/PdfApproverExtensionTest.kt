package io.pdf4k.testing

import com.lowagie.text.Document
import com.lowagie.text.Phrase
import com.lowagie.text.pdf.PdfWriter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pro.juxt.pdf4k.testing.PdfApprover
import pro.juxt.pdf4k.testing.PdfApproverExtension
import java.io.ByteArrayOutputStream

@ExtendWith(PdfApproverExtension::class)
class PdfApproverExtensionTest {
    @Test
    fun `checks two PDFs are equal`(approver: PdfApprover) {
        approver.assertApproved(actual("IDENTICAL").toByteArray())
    }

    @ParameterizedTest
    @ValueSource(strings = ["String 1", "String 2"])
    fun `handles parameterized tests`(string: String, approver: PdfApprover) {
        approver.assertApproved(actual(string).toByteArray())
    }

    private fun actual(text: String): ByteArrayOutputStream {
        val actual = ByteArrayOutputStream().use { output ->
            Document().use { document ->
                PdfWriter.getInstance(document, output)
                document.open()
                document.add(Phrase(text))
            }
            output
        }
        return actual
    }
}