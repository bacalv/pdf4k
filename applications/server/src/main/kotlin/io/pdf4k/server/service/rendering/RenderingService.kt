package io.pdf4k.server.service.rendering

import io.pdf4k.domain.Pdf
import io.pdf4k.domain.ResourceLocation.Companion.classpathResource
import io.pdf4k.domain.ResourceType.*
import io.pdf4k.domain.dto.ResourceMap
import io.pdf4k.provider.FontProviderFactory
import io.pdf4k.provider.ResourceLocators
import io.pdf4k.provider.TempFileFactory.Companion.defaultTempFileFactory
import io.pdf4k.provider.TempStreamFactory
import io.pdf4k.provider.UriResourceLoader.Companion.defaultResourceLoader
import io.pdf4k.renderer.DocumentAssembler
import io.pdf4k.renderer.PdfRenderer
import io.pdf4k.server.domain.StationaryPack
import io.pdf4k.server.service.MultipartFileStore
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class RenderingService(
    private val tempStreamFactory: TempStreamFactory,
    private val documentAssembler: DocumentAssembler,
    private val multipartFileStore: MultipartFileStore
) {
    fun render(stationaryPack: StationaryPack, pdf: Pdf, resourceMap: ResourceMap): InputStream {
        val fontProviderFactory = FontProviderFactory(defaultTempFileFactory)
        val resourceLocators = ResourceLocators(defaultResourceLoader, emptyMap(), fontProviderFactory) { type, name ->
            when (type) {
                PageTemplate -> stationaryPack.pageTemplates[name]?.value
                Font -> stationaryPack.fonts[name]?.value
                Image -> stationaryPack.images[name]?.value
            }?.let { multipartFileStore.get(it) }
                ?: classpathResource(type(name))
        }
        val pdfRenderer = PdfRenderer(resourceLocators, tempStreamFactory, documentAssembler)
        val outputStream = ByteArrayOutputStream()
        pdfRenderer.render(pdf, outputStream)
        return ByteArrayInputStream(outputStream.toByteArray())
    }
}