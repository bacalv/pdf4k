package io.pdf4k.domain

data class TableAttributes(
    val columns: Int,
    val widthPercentage: Float?,
    val weights: FloatArray?,
    val margin: Margin?,
    val headerRows: Int
)
