package io.pdf4k.provider

import com.lowagie.text.pdf.PdfReader
import io.pdf4k.domain.LoadedStationary
import io.pdf4k.domain.Section

object StationaryLoader {
    fun ResourceLocator.loadStationary(sections: List<Section>) = sections.map { it.stationary }.flatten().toSet()
        .associateWith { LoadedStationary(it, PdfReader(load(it.template))) }
}
