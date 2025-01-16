package io.pdf4k.renderer

import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.PdfStamper
import java.io.OutputStream

class PdfStamperAccessor(reader: PdfReader, outputStream: OutputStream) : PdfStamper(reader, outputStream) {
}