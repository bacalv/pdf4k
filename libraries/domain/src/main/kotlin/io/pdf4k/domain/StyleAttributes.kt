package io.pdf4k.domain

import io.pdf4k.domain.Leading.Companion.leading
import java.awt.Color

data class StyleAttributes(
    val font: Font? = null,
    val fontStyle: Font.Style? = null,
    val size: Float? = null,
    val colour: Color? = null,
    val background: Color? = null,
    val underlined: Boolean? = null,
    val underlineColour: Color? = null,
    val leading: Leading? = null,
    val align: HorizontalAlignment? = null,
    val valign: VerticalAlignment? = null,
    val cellBackground: Color? = null,
    val paddingTop: Float? = null,
    val paddingBottom: Float? = null,
    val paddingLeft: Float? = null,
    val paddingRight: Float? = null,
    val borderWidthTop: Float? = null,
    val borderWidthBottom: Float? = null,
    val borderWidthLeft: Float? = null,
    val borderWidthRight: Float? = null,
    val borderColourTop: Color? = null,
    val borderColourBottom: Color? = null,
    val borderColourLeft: Color? = null,
    val borderColourRight: Color? = null,
    val splitLate: Boolean? = null,
    val splitRows: Boolean? = null,
    val listStyle: ListStyle? = null
) {
    companion object {
        val DEFAULT_LEADING = leading(0f, 1.2f)

        fun style(
            font: Font? = null,
            fontStyle: Font.Style? = null,
            size: Float? = null,
            colour: Color? = null,
            background: Color? = null,
            underlined: Boolean? = null,
            underlineColour: Color? = null,
            leading: Leading? = null,
            align: HorizontalAlignment? = null,
            valign: VerticalAlignment? = null,
            cellBackground: Color? = null,
            paddingTop: Float? = null,
            paddingBottom: Float? = null,
            paddingLeft: Float? = null,
            paddingRight: Float? = null,
            borderWidthTop: Float? = null,
            borderWidthBottom: Float? = null,
            borderWidthLeft: Float? = null,
            borderWidthRight: Float? = null,
            borderColourTop: Color? = null,
            borderColourBottom: Color? = null,
            borderColourLeft: Color? = null,
            borderColourRight: Color? = null,
            splitLate: Boolean? = null,
            splitRows: Boolean? = null,
            listStyle: ListStyle? = null
        ) = StyleAttributes(
            font,
            fontStyle,
            size,
            colour,
            background,
            underlined,
            underlineColour,
            leading,
            align,
            valign,
            cellBackground,
            paddingTop,
            paddingBottom,
            paddingLeft,
            paddingRight,
            borderWidthTop,
            borderWidthBottom,
            borderWidthLeft,
            borderWidthRight,
            borderColourTop,
            borderColourBottom,
            borderColourLeft,
            borderColourRight,
            splitLate,
            splitRows,
            listStyle
        )

        fun border(width: Float, colour: Color? = null) = StyleAttributes(
            borderWidthTop = width,
            borderWidthBottom = width,
            borderWidthLeft = width,
            borderWidthRight = width,
            borderColourTop = colour,
            borderColourBottom = colour,
            borderColourLeft = colour,
            borderColourRight = colour,
        )

        val noBorder = border(0f)

        fun padding(n: Float) = style(paddingTop = n, paddingBottom = n, paddingLeft = n, paddingRight = n)
    }

    operator fun plus(other: StyleAttributes) = StyleAttributes(
        other.font ?: font,
        other.fontStyle ?: fontStyle,
        other.size ?: size,
        other.colour ?: colour,
        other.background ?: background,
        other.underlined ?: underlined,
        other.underlineColour ?: underlineColour,
        other.leading ?: leading,
        other.align ?: align,
        other.valign ?: valign,
        other.cellBackground ?: cellBackground,
        other.paddingTop ?: paddingTop,
        other.paddingBottom ?: paddingBottom,
        other.paddingLeft ?: paddingLeft,
        other.paddingRight ?: paddingRight,
        other.borderWidthTop ?: borderWidthTop,
        other.borderWidthBottom ?: borderWidthBottom,
        other.borderWidthLeft ?: borderWidthLeft,
        other.borderWidthRight ?: borderWidthRight,
        other.borderColourTop ?: borderColourTop,
        other.borderColourBottom ?: borderColourBottom,
        other.borderColourLeft ?: borderColourLeft,
        other.borderColourRight ?: borderColourRight,
        other.splitLate ?: splitLate,
        other.splitRows ?: splitRows,
        other.listStyle ?: listStyle
    )
}