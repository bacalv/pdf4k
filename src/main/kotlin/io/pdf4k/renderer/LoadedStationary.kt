package io.pdf4k.renderer

import com.lowagie.text.pdf.PdfReader
import io.pdf4k.domain.Stationary

data class LoadedStationary(
    val stationary: Stationary,
    val reader: PdfReader
)