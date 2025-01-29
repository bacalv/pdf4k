package io.pdf4k.renderer

sealed class PdfError {
    data class PageTemplateNotFound(val templateName: String): PdfError()
    object KeyParseError: PdfError()
}