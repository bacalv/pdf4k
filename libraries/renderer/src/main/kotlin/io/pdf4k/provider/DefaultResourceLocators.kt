package io.pdf4k.provider

import io.pdf4k.domain.PdfError.*
import io.pdf4k.domain.ResourceType
import io.pdf4k.domain.ResourceType.*
import java.io.InputStream

interface ResourceLocators {
    val imageResourceLocator: ResourceLocator
    val stationaryResourceLocator: ResourceLocator
    val fontProvider: FontProvider
}

class DefaultResourceLocators(
    uriResourceLoader: UriResourceLoader,
    customProviders: Map<String, CustomResourceProvider>,
    fontProviderFactory: FontProviderFactory,
    loadResource: (ResourceType, String) -> InputStream? = classpathResourceLoader
): ResourceLocators {
    companion object {
        val classpathResourceLoader: (ResourceType, String) -> InputStream? = { type, name ->
            ResourceLocator::class.java.getResourceAsStream(type(name))
        }
    }
    override val imageResourceLocator = DefaultResourceLocator(Image, uriResourceLoader, customProviders, loadResource, ::ImageNotFound)
    override val stationaryResourceLocator = DefaultResourceLocator(PageTemplate, uriResourceLoader, customProviders, loadResource, ::PageTemplateNotFound)
    private val fontResourceLocator = DefaultResourceLocator(Font, uriResourceLoader, customProviders, loadResource, ::FontNotFound)
    override val fontProvider = fontProviderFactory.newFontProvider(fontResourceLocator)
}