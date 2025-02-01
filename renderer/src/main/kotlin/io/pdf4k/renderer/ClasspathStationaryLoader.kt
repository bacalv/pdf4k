package io.pdf4k.renderer

import com.lowagie.text.pdf.PdfReader
import io.pdf4k.domain.*
import io.pdf4k.renderer.PdfError.PageTemplateNotFound

object ClasspathStationaryLoader: StationaryLoader {
    override fun loadStationary(stationaryList: List<Stationary>): PdfOutcome<Map<Stationary, LoadedStationary>> =
        success(stationaryList.associateWith { stationary ->
            val stream = PdfRenderer::class.java.getResourceAsStream("/stationary/${stationary.template}.pdf")
                ?: return failure(PageTemplateNotFound(stationary.template))
            val reader = PdfReader(stream)
            LoadedStationary(stationary, reader)
        })
}
