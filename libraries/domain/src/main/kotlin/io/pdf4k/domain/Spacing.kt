package io.pdf4k.domain

sealed interface Spacing {
    data class Fixed(val fixed: Float) : Spacing

    data class Lines(val lines: Float) : Spacing

    companion object {
        val ZERO = Fixed(0f)
    }
}