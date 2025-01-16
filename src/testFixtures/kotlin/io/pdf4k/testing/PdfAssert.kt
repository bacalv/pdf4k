package io.pdf4k.testing

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.min

object PdfAssert {
    fun assertEquals(approved: ByteArray, actual: ByteArray) {
        val approvedDocument = Loader.loadPDF(approved)
        val actualDocument = Loader.loadPDF(actual)

        assertAll(
            "PDF Comparison Failures",
            imageComparisonAssertions(approvedDocument, actualDocument)
                    + metadataAssertions(approvedDocument, actualDocument)
                    + annotationAssertions(approvedDocument, actualDocument)
        )
    }

    private fun metadataAssertions(approvedDocument: PDDocument, actualDocument: PDDocument): List<() -> Unit> {
        return approvedDocument.documentInformation.metadataKeys.filterNot { it in setOf("CreationDate", "ModDate") }.map { key ->
            {
                assertEquals(
                    approvedDocument.documentInformation.getPropertyStringValue(key),
                    actualDocument.documentInformation.getPropertyStringValue(key),
                    "Metadata property $key matches"
                )
            }
        }
    }

    private fun annotationAssertions(approvedDocument: PDDocument, actualDocument: PDDocument): List<() -> Unit> {
        return approvedDocument.documentCatalog.pages.mapIndexed { index, pdPage ->
            { assertEquals(pdPage.annotations.toString(), actualDocument.documentCatalog.pages.get(index).annotations.toString()) }
        }
    }

    private fun imageComparisonAssertions(approvedDocument: PDDocument, actualDocument: PDDocument): List<() -> Unit> {
        val approvedRenderer = PDFRenderer(approvedDocument)
        val actualRenderer = PDFRenderer(actualDocument)
        return (0..< min(actualDocument.numberOfPages, approvedDocument.numberOfPages)).map { page ->
            val approvedPage = approvedRenderer.renderImageWithDPI(page, 300f)
            val actualPage = actualRenderer.renderImageWithDPI(page, 300f)
            val approvedOut = ByteArrayOutputStream()
            val actualOut = ByteArrayOutputStream()
            ImageIO.write(approvedPage, "png", approvedOut)
            ImageIO.write(actualPage, "png", actualOut)
            val match = approvedOut.toByteArray().contentEquals(actualOut.toByteArray())
            val assertion = { assertTrue(match, "Page ${page + 1} images match") }
            assertion
        } + { assertEquals(approvedDocument.numberOfPages, actualDocument.numberOfPages, "Wrong number of pages") }
    }
}