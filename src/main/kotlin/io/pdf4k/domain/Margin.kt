package io.pdf4k.domain

data class Margin(val top: Float, val bottom: Float, val left: Float, val right: Float) {
    fun toBlock(width: Float, height: Float) = Block(
        x = left,
        y = bottom,
        w = width - (left + right),
        h = height - (top + bottom)
    )

    companion object {
        val DEFAULT_MARGIN = Margin(36f, 36f, 36f, 36f)
        val ZERO = Margin(0f, 0f, 0f, 0f)
    }
}
