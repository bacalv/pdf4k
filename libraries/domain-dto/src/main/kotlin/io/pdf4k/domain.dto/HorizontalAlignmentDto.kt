package io.pdf4k.domain.dto

import io.pdf4k.domain.HorizontalAlignment

enum class HorizontalAlignmentDto {
    Left,
    Center,
    Right,
    Justified,
    JustifiedAll
}

fun HorizontalAlignment.toDto() = when (this) {
    HorizontalAlignment.Left -> HorizontalAlignmentDto.Left
    HorizontalAlignment.Center -> HorizontalAlignmentDto.Center
    HorizontalAlignment.Right -> HorizontalAlignmentDto.Right
    HorizontalAlignment.Justified -> HorizontalAlignmentDto.Justified
    HorizontalAlignment.JustifiedAll -> HorizontalAlignmentDto.JustifiedAll
}

fun HorizontalAlignmentDto.toDomain() = when (this) {
    HorizontalAlignmentDto.Left -> HorizontalAlignment.Left
    HorizontalAlignmentDto.Center -> HorizontalAlignment.Center
    HorizontalAlignmentDto.Right -> HorizontalAlignment.Right
    HorizontalAlignmentDto.Justified -> HorizontalAlignment.Justified
    HorizontalAlignmentDto.JustifiedAll -> HorizontalAlignment.JustifiedAll
}