package io.pdf4k.provider

import io.pdf4k.domain.PdfError
import io.pdf4k.domain.PdfError.CustomResourceProviderNotFound
import io.pdf4k.domain.ResourceLocation
import io.pdf4k.domain.ResourceLocation.Local
import io.pdf4k.domain.ResourceLocation.Remote
import io.pdf4k.domain.ResourceLocation.Remote.Custom
import io.pdf4k.domain.ResourceLocation.Remote.Uri
import java.io.InputStream

class ResourceLocator(
    private val rootDir: String,
    private val uriResourceLoader: UriResourceLoader,
    private val customProviders: Map<String, CustomResourceProvider>,
    private val localLoader: (String, String) -> InputStream?,
    private val nameMapper: (String) -> String = { it },
    private val notFound: (ResourceLocation) -> PdfError
) {
    fun load(location: ResourceLocation): InputStream = when (location) {
        is Local -> localLoader(rootDir, nameMapper(location.name))
        is Remote -> when (location) {
            is Uri -> uriResourceLoader.load(location.uri)
            is Custom -> loadFromCustomProvider(location)
        }
    } ?: throw notFound(location)

    private fun loadFromCustomProvider(location: Custom): InputStream {
        val provider = customProviders[location.providerName] ?:
            throw CustomResourceProviderNotFound(location.providerName)
        val result = provider.load(location.name)
        return result ?: throw notFound(location)
    }
}