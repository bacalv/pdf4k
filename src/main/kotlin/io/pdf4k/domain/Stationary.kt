package io.pdf4k.domain

import io.pdf4k.domain.Margin.Companion.DEFAULT_MARGIN

data class Stationary(val template: String, val templatePage: Int = 1, val margin: Margin = DEFAULT_MARGIN,
                      val blocks: Map<String, Rectangle> = emptyMap()) {
    companion object {
        val BlankA4Portrait = Stationary("blank-a4-portrait", 1, DEFAULT_MARGIN)
    }
}
