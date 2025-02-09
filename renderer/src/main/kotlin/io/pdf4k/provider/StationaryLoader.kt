package io.pdf4k.provider

import com.lowagie.text.pdf.PdfReader
import io.pdf4k.domain.LoadedStationary
import io.pdf4k.domain.Page

object StationaryLoader {
    fun ResourceLocator.loadStationary(pages: List<Page>) = pages.map { it.stationary }.flatten().toSet()
        .associateWith { LoadedStationary(it, PdfReader(load(it.template))) }
}
