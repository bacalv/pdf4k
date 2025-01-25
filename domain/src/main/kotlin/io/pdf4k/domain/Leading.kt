package io.pdf4k.domain

data class Leading(val fixed: Float = 0f, val multiplier: Float = 0f) {
    companion object {
        fun fixed(amount: Float) = Leading(amount, 0f)
        fun multiplier(amount: Float) = Leading(0f, amount)
        fun leading(fixed: Float, multiplier: Float) = Leading(fixed, multiplier)
    }
}