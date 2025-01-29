package io.pdf4k.renderer

sealed class PdfError {
    data class PageTemplateNotFound(val templateName: String): PdfError()
    data object KeyParseError: PdfError()
//    data class FontNotFound(val name: String): PdfError()
}