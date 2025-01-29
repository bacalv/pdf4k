package io.pdf4k.renderer

import io.pdf4k.domain.PdfOutcome
import io.pdf4k.domain.Stationary

interface StationaryLoader {
    fun loadStationary(stationaryList: List<Stationary>): PdfOutcome<Map<Stationary, LoadedStationary>>
}
