package io.pdf4k.domain

import io.pdf4k.domain.Margin.Companion.DEFAULT_MARGIN

data class Stationary(
    val template: String,
    val templatePage: Int = 1,
    val margin: Margin = DEFAULT_MARGIN,
    val blocks: Map<String, Block> = emptyMap(),
    val contentFlow: List<String> = emptyList()
) {
    companion object {
        val BlankA4Landscape = Stationary("blank-a4-landscape", 1, DEFAULT_MARGIN, contentFlow = emptyList())
        val BlankA4Portrait = Stationary("blank-a4-portrait", 1, DEFAULT_MARGIN, contentFlow = emptyList())
    }
}
