package io.pdf4k.domain.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.pdf4k.domain.Spacing

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    Type(value = SpacingDto.Fixed::class, name = "fixed"),
    Type(value = SpacingDto.Lines::class, name = "lines")
)
sealed interface SpacingDto {
    data class Fixed(val fixed: Float) : SpacingDto

    data class Lines(val lines: Float) : SpacingDto
}

fun Spacing.toDto() = when (this) {
    is Spacing.Fixed -> SpacingDto.Fixed(fixed)
    is Spacing.Lines -> SpacingDto.Lines(lines)
}

fun SpacingDto.toDomain() = when (this) {
    is SpacingDto.Fixed -> Spacing.Fixed(fixed)
    is SpacingDto.Lines -> Spacing.Lines(lines)
}