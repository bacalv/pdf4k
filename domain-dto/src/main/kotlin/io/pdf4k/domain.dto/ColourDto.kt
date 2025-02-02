package io.pdf4k.domain.dto

import java.awt.Color

typealias ColourDto = String

fun Color.toDto(): ColourDto = twoDigitHex(red) + twoDigitHex(green) + twoDigitHex(blue) + twoDigitHex(alpha)

private fun twoDigitHex(n: Int) = n.toString(16).padStart(2, '0')