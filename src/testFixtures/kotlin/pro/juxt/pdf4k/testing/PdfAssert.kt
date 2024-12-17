package pro.juxt.pdf4k.testing

import com.lowagie.text.pdf.PdfReader
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.min

object PdfAssert {
    fun assertEquals(approved: ByteArray, actual: ByteArray) {
        assertAll(
            "PDF Comparison Failures",
            pageContentAssertions(approved, actual) + imageComparisonAssertions(approved, actual)
        )
    }

    private fun pageContentAssertions(approved: ByteArray, actual: ByteArray): List<() -> Unit> {
        val actualPdf = PdfReader(actual)
        val approvedPdf = PdfReader(approved)

        return (1..min(approvedPdf.numberOfPages, actualPdf.numberOfPages)).map {
            {
                assertArrayEquals(
                    approvedPdf.getPageContent(it),
                    actualPdf.getPageContent(it),
                    "Page $it content differs"
                )
            }
        } + { assertEquals(approvedPdf.numberOfPages, actualPdf.numberOfPages, "Number of pages differ") }
    }

    private fun imageComparisonAssertions(approved: ByteArray, actual: ByteArray): List<() -> Unit> {
        val approvedDocument = Loader.loadPDF(approved)
        val actualDocument = Loader.loadPDF(actual)
        val approvedRenderer = PDFRenderer(approvedDocument)
        val actualRenderer = PDFRenderer(actualDocument)
        return (0..< approvedDocument.numberOfPages).map { page ->
            val approvedPage = approvedRenderer.renderImageWithDPI(page, 300f)
            val actualPage = actualRenderer.renderImageWithDPI(page, 300f)
            val approvedOut = ByteArrayOutputStream()
            val actualOut = ByteArrayOutputStream()
            ImageIO.write(approvedPage, "png", approvedOut)
            ImageIO.write(actualPage, "png", actualOut)
            val match = approvedOut.toByteArray().contentEquals(actualOut.toByteArray())
            val assertion = { assertTrue(match, "Page ${page + 1} images match") }
            assertion
        }
    }
}