package io.pdf4k.provider

import io.pdf4k.renderer.PdfError.ImageNotFound
import io.pdf4k.renderer.PdfError.PageTemplateNotFound

class ResourceLocators(
    uriResourceLoader: UriResourceLoader,
    customProviders: Map<String, CustomResourceProvider>,
    fontProviderFactory: FontProviderFactory
) {
    val imageResourceLocator = ResourceLocator("images", uriResourceLoader, customProviders, { it }, ::ImageNotFound)
    val stationaryResourceLocator = ResourceLocator("stationary", uriResourceLoader, customProviders, { "$it.pdf" }, ::PageTemplateNotFound)
    private val fontResourceLocator = ResourceLocator("fonts", uriResourceLoader, customProviders, { it }, ::ImageNotFound)
    val fontProvider = fontProviderFactory.newFontProvider(fontResourceLocator)
}