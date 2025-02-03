package io.pdf4k.domain.dto

import java.awt.Color

typealias ColourDto = String

fun Color.toDto(): ColourDto = twoDigitHex(red) + twoDigitHex(green) + twoDigitHex(blue) + twoDigitHex(alpha)

fun ColourDto.toDomain(): Color = Color(
    substring(0, 2).toInt(16),
    substring(2, 4).toInt(16),
    substring(4, 6).toInt(16),
    substring(6, 8).toInt(16)
)

private fun twoDigitHex(n: Int) = n.toString(16).padStart(2, '0')