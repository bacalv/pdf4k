package io.pdf4k.domain

sealed class PdfError : RuntimeException() {
    data class PageTemplateNotFound(val resource: ResourceLocation): PdfError()
    data class RenderingError(override val cause: Throwable?) : PdfError()
    class KeyParseError: PdfError()
    data class FontNotFound(val resource: ResourceLocation): PdfError()
    data class ImageNotFound(val resource: ResourceLocation): PdfError()
    data class KeyNotFound(val keyName: String): PdfError()
    data class CustomResourceProviderNotFound(val providerName: String): PdfError()
    data class ClasspathResourceNotFound(val path: String): PdfError()
}