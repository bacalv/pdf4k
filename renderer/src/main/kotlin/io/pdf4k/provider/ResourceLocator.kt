package io.pdf4k.provider

import io.pdf4k.domain.Outcome.Failure
import io.pdf4k.domain.Outcome.Success
import io.pdf4k.domain.PdfOutcome
import io.pdf4k.domain.ResourceLocation
import io.pdf4k.domain.ResourceLocation.Local
import io.pdf4k.domain.ResourceLocation.Remote
import io.pdf4k.domain.ResourceLocation.Remote.Custom
import io.pdf4k.domain.ResourceLocation.Remote.Uri
import io.pdf4k.domain.asFailure
import io.pdf4k.domain.asSuccess
import io.pdf4k.renderer.PdfError
import io.pdf4k.renderer.PdfError.CustomResourceProviderNotFound
import java.io.InputStream

class ResourceLocator(
    private val rootDir: String,
    private val uriResourceLoader: UriResourceLoader,
    private val customProviders: Map<String, CustomResourceProvider>,
    private val nameMapper: (String) -> String = { it },
    private val notFound: (ResourceLocation) -> PdfError
) {
    fun load(location: ResourceLocation): PdfOutcome<InputStream> = when (location) {
        is Local -> this::class.java.getResourceAsStream("/$rootDir/${nameMapper(location.name)}")?.asSuccess()
        is Remote -> when (location) {
            is Uri -> uriResourceLoader.load(location.uri)
            is Custom -> loadFromCustomProvider(location)
        }
    } ?: notFound(location).asFailure()

    private fun loadFromCustomProvider(location: Custom): PdfOutcome<InputStream> {
        val provider = customProviders[location.providerName] ?:
            return Failure(CustomResourceProviderNotFound(location.providerName))
        val result = provider.load(location.name)
        return result?.let { Success(it) } ?: Failure(notFound(location))
    }
}