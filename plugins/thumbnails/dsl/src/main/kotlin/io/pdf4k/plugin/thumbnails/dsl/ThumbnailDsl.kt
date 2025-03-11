package io.pdf4k.plugin.thumbnails.dsl

import io.pdf4k.domain.ResourceLocation
import io.pdf4k.domain.ResourceLocation.Companion.custom
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.toArgument
import io.pdf4k.dsl.AnyPhraseBuilder
import io.pdf4k.dsl.AnyTableBuilder
import io.pdf4k.plugin.thumbnails.domain.thumbnailsProviderName

fun thumbnailResource(
    resource: ResourceLocation,
    width: Int,
    height: Int
) = custom(
    providerName = thumbnailsProviderName,
    resource.toArgument("resource"),
    width.toArgument("width"),
    height.toArgument("height")
)

fun AnyPhraseBuilder.thumbnail(
    resource: ResourceLocation,
    width: Int,
    height: Int
) {
    image(thumbnailResource(resource, width, height), width = width.toFloat(), height = height.toFloat())
}

fun AnyTableBuilder.thumbnail(
    resource: ResourceLocation,
    width: Int,
    height: Int,
    style: StyleAttributes? = null,
    colSpan: Int = 1,
    rowSpan: Int = 1
) {
    imageCell(thumbnailResource(resource, width, height), style, colSpan, rowSpan, width.toFloat(), height.toFloat())
}