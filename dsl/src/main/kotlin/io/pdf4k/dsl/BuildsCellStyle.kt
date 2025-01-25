package io.pdf4k.dsl

import io.pdf4k.domain.*
import java.awt.Color

interface BuildsCellStyle<T : Component, B : ComponentBuilder<T, B>> : ComponentBuilder<T, B> {
    val childBuilder: () -> B

    fun style(
        font: Font? = null,
        fontStyle: Font.Style? = null,
        size: Float? = null,
        colour: Color? = null,
        background: Color? = null,
        underline: Boolean = false,
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
        block: B.() -> Unit
    ) = style(
        StyleAttributes(
            font,
            fontStyle,
            size,
            colour,
            background,
            underline,
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
        ), block
    )

    fun style(attributes: StyleAttributes, block: B.() -> Unit) {
        addChild(
            StyleBuilder(
                attributes,
                childBuilder().also { it.block() }.children
            )
        )
    }
}