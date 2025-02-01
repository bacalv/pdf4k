package io.pdf4k.provider

import io.pdf4k.renderer.PdfError
import io.pdf4k.renderer.StationaryLoader

class ResourceLocators(
    uriResourceLoader: UriResourceLoader,
    customProviders: Map<String, CustomResourceProvider>,
    val fontProvider: FontProvider,
    val stationaryLoader: StationaryLoader
) {
    val imageResourceLocator = ResourceLocator("images", uriResourceLoader, customProviders, PdfError::ImageNotFound)
}