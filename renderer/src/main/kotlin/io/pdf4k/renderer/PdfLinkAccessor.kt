package io.pdf4k.renderer

import com.lowagie.text.pdf.PdfAnnotation.PdfImportedLink
import com.lowagie.text.pdf.PdfDictionary
import com.lowagie.text.pdf.PdfIndirectReference
import com.lowagie.text.pdf.PdfName

class PdfLinkAccessor(val link: PdfImportedLink) {
    fun isLocal() = (getParams()[PdfName.A] as PdfDictionary)[PdfName.S] == PdfName.GOTO
    fun getReference() = (getParams()[PdfName.A] as PdfDictionary)[PdfName.D] as PdfIndirectReference

    private fun getParams() = link::class.java.getDeclaredField("parameters").also { it.trySetAccessible() }.get(link) as Map<*, *>
}