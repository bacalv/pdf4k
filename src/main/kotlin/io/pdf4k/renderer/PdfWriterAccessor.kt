package io.pdf4k.renderer

import com.lowagie.text.pdf.*
import java.util.*

class PdfWriterAccessor(private val writer: PdfWriter) {
    fun writeNamedDestinations() {
        val doc = PdfWriter::class.java.getDeclaredField("pdf").also { it.trySetAccessible() }.get(writer) as PdfDocument
        val destinations = PdfDocument::class.java.getDeclaredField("localDestinations").also { it.trySetAccessible() }.get(doc)
        PdfWriter::class.java.getDeclaredMethod("addLocalDestinations", TreeMap::class.java).also { it.trySetAccessible() }.invoke(writer, destinations)
    }

    fun addNamedDestination(name: Any, ref: PdfIndirectObject) {
        val catalog = writer.extraCatalog
        val level1 = PdfDictionary()
        val level2 = PdfDictionary()
        val names = PdfArray(mutableListOf<PdfObject>(PdfString(name.toString()), ref.indirectReference))
        level2.put(PdfName.NAMES, names)
        level1.put(PdfName.DESTS, writer.addToBody(level2).indirectReference)
        catalog.put(PdfName.NAMES, writer.addToBody(level1).indirectReference)
    }
}