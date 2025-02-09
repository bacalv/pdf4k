package io.pdf4k.provider

import io.pdf4k.domain.PdfError.*
import java.io.InputStream

class ResourceLocators(
    uriResourceLoader: UriResourceLoader,
    customProviders: Map<String, CustomResourceProvider>,
    fontProviderFactory: FontProviderFactory,
    stationaryResourceLocatorNameMapper: (String) -> String = { "$it.pdf" },
    localLoader: (String, String) -> InputStream? = { rootDir, it -> ResourceLocator::class.java.getResourceAsStream("/$rootDir/$it") }
) {
    val imageResourceLocator = ResourceLocator("images", uriResourceLoader, customProviders, localLoader, { it }, ::ImageNotFound)
    val stationaryResourceLocator = ResourceLocator("stationary", uriResourceLoader, customProviders, localLoader, stationaryResourceLocatorNameMapper, ::PageTemplateNotFound)
    private val fontResourceLocator = ResourceLocator("fonts", uriResourceLoader, customProviders, localLoader, { it }, ::FontNotFound)
    val fontProvider = fontProviderFactory.newFontProvider(fontResourceLocator)
}