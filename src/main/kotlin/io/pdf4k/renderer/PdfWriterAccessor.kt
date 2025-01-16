package io.pdf4k.renderer

import com.lowagie.text.pdf.PdfDocument
import com.lowagie.text.pdf.PdfWriter
import java.util.*

class PdfWriterAccessor(private val writer: PdfWriter) {
    fun writeNamedDestinations() {
        val doc = PdfWriter::class.java.getDeclaredField("pdf").also { it.trySetAccessible() }.get(writer) as PdfDocument
        val destinations = PdfDocument::class.java.getDeclaredField("localDestinations").also { it.trySetAccessible() }.get(doc)
        PdfWriter::class.java.getDeclaredMethod("addLocalDestinations", TreeMap::class.java).also { it.trySetAccessible() }.invoke(writer, destinations)
    }
}