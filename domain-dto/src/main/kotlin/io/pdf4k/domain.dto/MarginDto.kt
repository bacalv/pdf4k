package io.pdf4k.domain.dto

import io.pdf4k.domain.Margin

data class MarginDto(val top: Float, val bottom: Float, val left: Float, val right: Float)

fun Margin.toDto() = MarginDto(top, bottom, left, right)