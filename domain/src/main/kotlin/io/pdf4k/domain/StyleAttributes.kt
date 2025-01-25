package io.pdf4k.domain

import io.pdf4k.domain.HorizontalAlignment.Left
import io.pdf4k.domain.Leading.Companion.leading
import io.pdf4k.domain.VerticalAlignment.Top
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
) {
    companion object {
        val DEFAULT_LEADING = leading(0f, 1.2f)
        val DEFAULT_STYLE = StyleAttributes(
            Font.BuiltIn.Ariel,
            Font.Style.Plain,
            12f,
            Color.BLACK,
            null,
            null,
            null,
            DEFAULT_LEADING,
            Left,
            Top,
            null,
            0f,
            5f,
            4f,
            4f
        )

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
            splitRows: Boolean? = null
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
            splitRows
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
    }

    operator fun plus(other: StyleAttributes) = StyleAttributes(
        other.font ?: font ?: DEFAULT_STYLE.font,
        other.fontStyle ?: fontStyle ?: DEFAULT_STYLE.fontStyle,
        other.size ?: size ?: DEFAULT_STYLE.size,
        other.colour ?: colour ?: DEFAULT_STYLE.colour,
        other.background ?: background ?: DEFAULT_STYLE.background,
        other.underlined ?: underlined ?: DEFAULT_STYLE.underlined,
        other.underlineColour ?: underlineColour ?: DEFAULT_STYLE.underlineColour,
        other.leading ?: leading ?: DEFAULT_STYLE.leading,
        other.align ?: align ?: DEFAULT_STYLE.align,
        other.valign ?: valign ?: DEFAULT_STYLE.valign,
        other.cellBackground ?: cellBackground ?: DEFAULT_STYLE.cellBackground,
        other.paddingTop ?: paddingTop ?: DEFAULT_STYLE.paddingTop,
        other.paddingBottom ?: paddingBottom ?: DEFAULT_STYLE.paddingBottom,
        other.paddingLeft ?: paddingLeft ?: DEFAULT_STYLE.paddingLeft,
        other.paddingRight ?: paddingRight ?: DEFAULT_STYLE.paddingRight,
        other.borderWidthTop ?: borderWidthTop ?: DEFAULT_STYLE.borderWidthTop,
        other.borderWidthBottom ?: borderWidthBottom ?: DEFAULT_STYLE.borderWidthBottom,
        other.borderWidthLeft ?: borderWidthLeft ?: DEFAULT_STYLE.borderWidthLeft,
        other.borderWidthRight ?: borderWidthRight ?: DEFAULT_STYLE.borderWidthRight,
        other.borderColourTop ?: borderColourTop ?: DEFAULT_STYLE.borderColourTop,
        other.borderColourBottom ?: borderColourBottom ?: DEFAULT_STYLE.borderColourBottom,
        other.borderColourLeft ?: borderColourLeft ?: DEFAULT_STYLE.borderColourLeft,
        other.borderColourRight ?: borderColourRight ?: DEFAULT_STYLE.borderColourRight,
        other.splitLate ?: splitLate ?: DEFAULT_STYLE.splitLate,
        other.splitRows ?: splitRows ?: DEFAULT_STYLE.splitRows
    )
}