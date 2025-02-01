package io.pdf4k.domain

import java.awt.Color
import java.awt.Color.BLACK

data class QrStyle(val shape: Shape, val colour: Color = BLACK, val background: Color? = null, val size: Int = 25, val logo: Logo? = null) {

    companion object {
        enum class Shape {
            Square,
            Circle,
            RoundedSquare
        }

        data class Logo(val resource: ResourceLocation, val width: Int, val height: Int, val clearLogoArea: Boolean = true)
    }
}