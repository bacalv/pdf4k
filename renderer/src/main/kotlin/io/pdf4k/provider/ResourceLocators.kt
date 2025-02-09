package io.pdf4k.provider

import io.pdf4k.domain.PdfError.*
import io.pdf4k.domain.ResourceType
import io.pdf4k.domain.ResourceType.*
import java.io.InputStream

class ResourceLocators(
    uriResourceLoader: UriResourceLoader,
    customProviders: Map<String, CustomResourceProvider>,
    fontProviderFactory: FontProviderFactory,
    loadResource: (ResourceType, String) -> InputStream? = { type, name -> ResourceLocator::class.java.getResourceAsStream("/${type.directory}/$name${type.suffix}") }
) {
    val imageResourceLocator = ResourceLocator(Image, uriResourceLoader, customProviders, loadResource, ::ImageNotFound)
    val stationaryResourceLocator = ResourceLocator(PageTemplate, uriResourceLoader, customProviders, loadResource, ::PageTemplateNotFound)
    private val fontResourceLocator = ResourceLocator(Font, uriResourceLoader, customProviders, loadResource, ::FontNotFound)
    val fontProvider = fontProviderFactory.newFontProvider(fontResourceLocator)
}