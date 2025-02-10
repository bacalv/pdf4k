package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.Font
import io.pdf4k.domain.Leading
import io.pdf4k.domain.StyleAttributes
import java.awt.Color

interface BuildsTextStyle<T : Component, B : ComponentBuilder<T, B>> : ComponentBuilder<T, B> {
    val childBuilder: () -> B

    fun style(
        font: Font? = null,
        fontStyle: Font.Style? = null,
        size: Float? = null,
        colour: Color? = null,
        background: Color? = null,
        underline: Boolean? = null,
        underlineColour: Color? = null,
        leading: Leading? = null,
        block: B.() -> Unit
    ) {
        style(
            StyleAttributes(
                font,
                fontStyle,
                size,
                colour,
                background,
                underline,
                underlineColour,
                leading
            ), block
        )
    }

    fun style(attributes: StyleAttributes, block: B.() -> Unit) {
        addChild(
            StyleBuilder(
                attributes,
                childBuilder().also { it.block() }.children
            )
        )
    }
}