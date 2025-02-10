package io.pdf4k.domain

data class TableAttributes(
    val columns: Int,
    val widthPercentage: Float?,
    val weights: List<Float>?,
    val margin: Margin?,
    val headerRows: Int?,
    val extend: Boolean?
)
