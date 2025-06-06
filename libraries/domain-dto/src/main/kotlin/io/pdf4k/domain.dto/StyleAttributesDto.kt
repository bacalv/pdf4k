package io.pdf4k.domain.dto

import io.pdf4k.domain.StyleAttributes

data class StyleAttributesDto(
    val font: FontRef? = null,
    val fontStyle: FontDto.Style? = null,
    val size: Float? = null,
    val colour: ColourRef? = null,
    val background: ColourRef? = null,
    val underlined: Boolean? = null,
    val underlineColour: ColourRef? = null,
    val leading: LeadingDto? = null,
    val align: HorizontalAlignmentDto? = null,
    val valign: VerticalAlignmentDto? = null,
    val cellBackground: ColourRef? = null,
    val paddingTop: Float? = null,
    val paddingBottom: Float? = null,
    val paddingLeft: Float? = null,
    val paddingRight: Float? = null,
    val borderWidthTop: Float? = null,
    val borderWidthBottom: Float? = null,
    val borderWidthLeft: Float? = null,
    val borderWidthRight: Float? = null,
    val borderColourTop: ColourRef? = null,
    val borderColourBottom: ColourRef? = null,
    val borderColourLeft: ColourRef? = null,
    val borderColourRight: ColourRef? = null,
    val splitLate: Boolean? = null,
    val splitRows: Boolean? = null,
    val listStyle: ListStyleDto? = null,
    val spacingBefore: SpacingDto? = null,
    val spacingAfter: SpacingDto? = null,
)

fun StyleAttributes.toDto(resourceMapBuilder: ResourceMapDto.Builder) = StyleAttributesDto(
    font = font?.toDto(resourceMapBuilder)?.let(resourceMapBuilder::fontRef),
    fontStyle = fontStyle?.toDto(),
    size = size,
    colour = colour?.toDto()?.let(resourceMapBuilder::colourRef),
    background = background?.toDto()?.let(resourceMapBuilder::colourRef),
    underlined = underlined,
    underlineColour = underlineColour?.toDto()?.let(resourceMapBuilder::colourRef),
    leading = leading?.toDto(),
    align = align?.toDto(),
    valign = valign?.toDto(),
    cellBackground = cellBackground?.toDto()?.let(resourceMapBuilder::colourRef),
    paddingTop = paddingTop,
    paddingBottom = paddingBottom,
    paddingLeft = paddingLeft,
    paddingRight = paddingRight,
    borderWidthTop = borderWidthTop,
    borderWidthBottom = borderWidthBottom,
    borderWidthLeft = borderWidthLeft,
    borderWidthRight = borderWidthRight,
    borderColourTop = borderColourTop?.toDto()?.let(resourceMapBuilder::colourRef),
    borderColourBottom = borderColourBottom?.toDto()?.let(resourceMapBuilder::colourRef),
    borderColourLeft = borderColourLeft?.toDto()?.let(resourceMapBuilder::colourRef),
    borderColourRight = borderColourRight?.toDto()?.let(resourceMapBuilder::colourRef),
    splitLate = splitLate,
    splitRows = splitRows,
    listStyle = listStyle?.toDto(),
    spacingBefore = spacingBefore?.toDto(),
    spacingAfter = spacingAfter?.toDto(),

)

fun StyleAttributesDto.toDomain(resourceMap: ResourceMap): StyleAttributes = StyleAttributes(
    font = font?.let { resourceMap.getFont(it) },
    fontStyle = fontStyle?.toDomain(),
    size = size,
    colour = colour?.let { resourceMap.getColour(it) },
    background = background?.let { resourceMap.getColour(it) },
    underlined = underlined,
    underlineColour = underlineColour?.let { resourceMap.getColour(it) },
    leading = leading?.toDomain(),
    align = align?.toDomain(),
    valign = valign?.toDomain(),
    cellBackground = cellBackground?.let { resourceMap.getColour(it) },
    paddingTop = paddingTop,
    paddingBottom = paddingBottom,
    paddingLeft = paddingLeft,
    paddingRight = paddingRight,
    borderWidthTop = borderWidthTop,
    borderWidthBottom = borderWidthBottom,
    borderWidthLeft = borderWidthLeft,
    borderWidthRight = borderWidthRight,
    borderColourTop = borderColourTop?.let { resourceMap.getColour(it) },
    borderColourBottom = borderColourBottom?.let { resourceMap.getColour(it) },
    borderColourLeft = borderColourLeft?.let { resourceMap.getColour(it) },
    borderColourRight = borderColourRight?.let { resourceMap.getColour(it) },
    splitLate = splitLate,
    splitRows = splitRows,
    listStyle = listStyle?.toDomain(),
    spacingBefore = spacingBefore?.toDomain(),
    spacingAfter = spacingAfter?.toDomain()
)