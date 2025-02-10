package io.pdf4k.domain.dto

import io.pdf4k.domain.Font
import io.pdf4k.domain.ResourceLocation
import io.pdf4k.domain.Stationary
import io.pdf4k.domain.StyleAttributes
import java.awt.Color

class ResourceMap(resourceMapDto: ResourceMapDto) {
    val resourceLocations: List<ResourceLocation> = resourceMapDto.resourceLocations.map { it.toDomain() }
    val colours: List<Color> = resourceMapDto.colours.map { it.toDomain() }
    val fonts: List<Font> = resourceMapDto.fonts.map { it.toDomain(this) }
    val styles: List<StyleAttributes> = resourceMapDto.styles.map { it.toDomain(this) }
    val stationary: List<Stationary> = resourceMapDto.stationary.map { it.toDomain(this) }

    fun getStyle(ref: StyleRef): StyleAttributes = styles[ref]
    fun getResourceLocation(ref: ResourceRef): ResourceLocation = resourceLocations[ref]
    fun getFont(font: FontRef): Font = fonts[font]
    fun getColour(colour: ColourRef) = colours[colour]
    fun getStationary(template: ResourceRef) = stationary[template]
}