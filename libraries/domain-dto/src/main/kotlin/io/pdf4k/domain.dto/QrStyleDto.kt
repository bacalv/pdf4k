package io.pdf4k.domain.dto

import io.pdf4k.domain.QrStyle
import io.pdf4k.domain.dto.QrStyleDto.Companion.Logo

data class QrStyleDto(val shape: Shape, val colour: ColourRef, val background: ColourRef?, val size: Int, val logo: Logo?) {
    companion object {
        enum class Shape {
            Square,
            Circle,
            RoundedSquare
        }

        data class Logo(val ref: ResourceRef, val width: Int, val height: Int, val clearLogoArea: Boolean)
    }
}

fun QrStyle.toDto(resourceMapBuilder: ResourceMapDto.Builder) = QrStyleDto(
    shape = shape.toDto(),
    colour = colour.toDto().let(resourceMapBuilder::colourRef),
    background = background?.toDto()?.let(resourceMapBuilder::colourRef),
    size = size,
    logo = logo?.run { Logo(resource.toDto().let(resourceMapBuilder::resourceRef), width, height, clearLogoArea) }
)

fun QrStyle.Companion.Shape.toDto() = when (this) {
    QrStyle.Companion.Shape.Square -> QrStyleDto.Companion.Shape.Square
    QrStyle.Companion.Shape.Circle -> QrStyleDto.Companion.Shape.Circle
    QrStyle.Companion.Shape.RoundedSquare -> QrStyleDto.Companion.Shape.RoundedSquare
}

fun QrStyleDto.toDomain(resourceMap: ResourceMap): QrStyle = QrStyle(
    shape = shape.toDomain(),
    colour = resourceMap.getColour(colour),
    background = background?.let { resourceMap.getColour(it) },
    size = size,
    logo = logo?.run { QrStyle.Companion.Logo(resourceMap.getResourceLocation(ref), width, height, clearLogoArea) }
)

fun QrStyleDto.Companion.Shape.toDomain() = when (this) {
    QrStyleDto.Companion.Shape.Square -> QrStyle.Companion.Shape.Square
    QrStyleDto.Companion.Shape.Circle -> QrStyle.Companion.Shape.Circle
    QrStyleDto.Companion.Shape.RoundedSquare -> QrStyle.Companion.Shape.RoundedSquare
}