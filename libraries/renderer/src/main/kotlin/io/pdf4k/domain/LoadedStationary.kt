package io.pdf4k.domain

import com.lowagie.text.pdf.PdfReader

data class LoadedStationary(
    val stationary: Stationary,
    val reader: PdfReader
)