package io.pdf4k.testing

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDDocumentCatalog
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination
import org.apache.pdfbox.rendering.PDFRenderer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertAll
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.min

object PdfAssert {
    private val ignoredMetadataKeys = setOf("CreationDate", "ModDate", "Producer")

    fun assertEquals(approved: ByteArray, actual: ByteArray, password: String = "") {
        val approvedDocument = Loader.loadPDF(approved, password)
        val actualDocument = Loader.loadPDF(actual, password)

        assertAll(
            "PDF Comparison Failures",
            imageComparisonAssertions(approvedDocument, actualDocument)
                    + metadataAssertions(approvedDocument, actualDocument)
                    + annotationAssertions(approvedDocument, actualDocument)
                    + namedDestinationAssertions(approvedDocument, actualDocument)
                    + encryptionAssertions(approvedDocument, actualDocument)
                    + permissionAssertions(approvedDocument, actualDocument)
        )
    }

    private fun encryptionAssertions(approvedDocument: PDDocument, actualDocument: PDDocument): List<() -> Unit> {
        val approvedDicts = approvedDocument.signatureDictionaries
        val actualDicts = actualDocument.signatureDictionaries
        val assertions = if (approvedDicts.size == actualDicts.size) {
            approvedDicts.mapIndexed { index, approved ->
                val actual = actualDicts[index]
                listOf(
                    { assertEquals(approved.name, actual.name, "Signature Name") },
                    { assertEquals(approved.location, actual.location, "Signature Location") },
                    { assertEquals(approved.contactInfo, actual.contactInfo, "Signature Contact") },
                    { assertEquals(approved.reason, actual.reason, "Signature Reason") },
                )
            }.flatten()
        } else emptyList()
        return listOf({ assertEquals(approvedDicts.size, actualDicts.size, "Number of signature dictionaries") }) + assertions
    }

    private fun permissionAssertions(approvedDocument: PDDocument, actualDocument: PDDocument): List<() -> Unit> {
        return listOf(
            { assertEquals(approvedDocument.currentAccessPermission.isOwnerPermission, actualDocument.currentAccessPermission.isOwnerPermission, "Owner permission flags match") },
            { assertEquals(approvedDocument.currentAccessPermission.permissionBytes, actualDocument.currentAccessPermission.permissionBytes, "Permissions match") },
        )
    }

    private fun namedDestinationAssertions(approvedDocument: PDDocument, actualDocument: PDDocument): List<() -> Unit> {
        return listOf({
            assertEquals(getNamedDestinations(approvedDocument), getNamedDestinations(actualDocument), "Named destinations match")
        })
    }

    private fun getNamedDestinations(doc: PDDocument): Set<NamedDestination> {
        val namedDestinations: MutableMap<String, PDPageDestination> = mutableMapOf()
        val documentCatalog: PDDocumentCatalog = doc.documentCatalog
        val names = documentCatalog.names ?: return emptySet()
        val dests = names.dests
        if (dests.names != null) namedDestinations.putAll(dests.names)
        val kids = dests.kids
        traverseKids(kids, namedDestinations)
        return namedDestinations.entries.map { it.key to it.value as? PDPageXYZDestination }
            .map { (k, v) -> v?.let { NamedDestination(k, v.cosObject[0].key.number, v.left, v.top, v.zoom) } }
            .filterNotNull()
            .toSet()
    }

    private fun traverseKids(
        kids: List<PDNameTreeNode<PDPageDestination>>?,
        namedDestinations: MutableMap<String, PDPageDestination>
    ) {
        if (kids == null) return
        for (kid in kids) {
            if (kid.names != null) {
                try {
                    namedDestinations.putAll(kid.names)
                } catch (e: Exception) {
                    fail("Duplicate named destination found.")
                }
            }
            if (kid.kids != null) traverseKids(kid.kids, namedDestinations)
        }
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
        } + listOf({ assertEquals(
            approvedDocument.documentInformation.metadataKeys - ignoredMetadataKeys,
            actualDocument.documentInformation.metadataKeys - ignoredMetadataKeys,
            "Same metadata keys"
        ) })
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

    private data class NamedDestination(val name: String, val page: Long, val left: Int, val top: Int, val zoom: Float)
}