package io.pdf4k.renderer

import io.pdf4k.domain.ResourceLocation

sealed class PdfError {
    data class PageTemplateNotFound(val templateName: String): PdfError()
    data class RenderingError(val cause: Throwable?) : PdfError()
    data object KeyParseError: PdfError()
    data class FontNotFound(val name: String): PdfError()
    data class ImageNotFound(val resource: ResourceLocation): PdfError()
    data class CustomResourceProviderNotFound(val providerName: String): PdfError()

    companion object {
        data class PdfErrorException(val error: PdfError) : RuntimeException()
    }
}