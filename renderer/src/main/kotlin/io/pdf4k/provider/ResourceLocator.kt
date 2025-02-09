package io.pdf4k.provider

import io.pdf4k.domain.PdfError
import io.pdf4k.domain.PdfError.CustomResourceProviderNotFound
import io.pdf4k.domain.ResourceLocation
import io.pdf4k.domain.ResourceLocation.Local
import io.pdf4k.domain.ResourceLocation.Remote
import io.pdf4k.domain.ResourceLocation.Remote.Custom
import io.pdf4k.domain.ResourceLocation.Remote.Uri
import io.pdf4k.domain.ResourceType
import java.io.InputStream

class ResourceLocator(
    private val type: ResourceType,
    private val uriResourceLoader: UriResourceLoader,
    private val customProviders: Map<String, CustomResourceProvider>,
    private val loadResource: (ResourceType, String) -> InputStream?,
    private val notFound: (ResourceLocation) -> PdfError
) {
    fun load(location: ResourceLocation): InputStream = when (location) {
        is Local -> loadResource(type, location.name)
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