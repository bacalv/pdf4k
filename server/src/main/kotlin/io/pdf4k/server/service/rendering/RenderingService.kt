package io.pdf4k.server.service.rendering

import io.pdf4k.domain.Pdf
import io.pdf4k.domain.dto.ResourceMap
import io.pdf4k.provider.FontProviderFactory
import io.pdf4k.provider.ResourceLocator
import io.pdf4k.provider.ResourceLocators
import io.pdf4k.provider.TempFileFactory.Companion.defaultTempFileFactory
import io.pdf4k.provider.TempStreamFactory
import io.pdf4k.provider.UriResourceLoader.Companion.defaultResourceLoader
import io.pdf4k.renderer.DocumentAssembler
import io.pdf4k.renderer.PdfRenderer
import io.pdf4k.server.service.MultipartFileStore
import io.pdf4k.server.service.realm.RealmService
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class RenderingService(
    private val realmService: RealmService,
    private val tempStreamFactory: TempStreamFactory,
    private val documentAssembler: DocumentAssembler,
    private val multipartFileStore: MultipartFileStore
) {
    fun render(realmName: String, stationaryPackName: String, pdf: Pdf, resourceMap: ResourceMap): InputStream {
        val stationaryPack = realmService.findStationaryPack(realmName, stationaryPackName)
        val outputStream = ByteArrayOutputStream()
        val fontProviderFactory = FontProviderFactory(defaultTempFileFactory)
        val resourceLocators = ResourceLocators(defaultResourceLoader, emptyMap(), fontProviderFactory, { it }) { type, name ->
            val extension = if (type == "stationary") ".pdf" else ""
            when (type) {
                "stationary" -> stationaryPack.pageTemplates[name]?.value
                "fonts" -> stationaryPack.fonts[name]?.value
                "images" -> stationaryPack.images[name]?.value
                else -> println(type).let { TODO() }
            }?.let { multipartFileStore.get(it) }
                ?: ResourceLocator::class.java.getResourceAsStream("/$type/$name$extension")
        }
        val pdfRenderer = PdfRenderer(resourceLocators, tempStreamFactory, documentAssembler)
        pdfRenderer.render(pdf, outputStream)
        return ByteArrayInputStream(outputStream.toByteArray())
    }
}