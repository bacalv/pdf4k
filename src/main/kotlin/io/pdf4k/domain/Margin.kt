package io.pdf4k.domain

data class Margin(val top: Float, val bottom: Float, val left: Float, val right: Float) {
    companion object {
        val DEFAULT_MARGIN = Margin(36f, 36f, 36f, 36f)
        val ZERO = Margin(0f, 0f, 0f, 0f)
    }
}
