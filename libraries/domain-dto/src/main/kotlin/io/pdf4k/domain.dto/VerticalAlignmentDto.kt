package io.pdf4k.domain.dto

import io.pdf4k.domain.VerticalAlignment

enum class VerticalAlignmentDto {
    Top,
    Middle,
    Bottom
}

fun VerticalAlignment.toDto() = when (this) {
    VerticalAlignment.Top -> VerticalAlignmentDto.Top
    VerticalAlignment.Middle -> VerticalAlignmentDto.Middle
    VerticalAlignment.Bottom -> VerticalAlignmentDto.Bottom
}

fun VerticalAlignmentDto.toDomain() = when (this) {
    VerticalAlignmentDto.Top -> VerticalAlignment.Top
    VerticalAlignmentDto.Middle -> VerticalAlignment.Middle
    VerticalAlignmentDto.Bottom -> VerticalAlignment.Bottom
}