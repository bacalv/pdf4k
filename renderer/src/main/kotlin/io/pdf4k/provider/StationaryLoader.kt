package io.pdf4k.provider

import com.lowagie.text.pdf.PdfReader
import io.pdf4k.domain.*

object StationaryLoader {
    fun ResourceLocator.loadStationary(pages: List<Page>): PdfOutcome<Map<Stationary, LoadedStationary>> {
        val empty: PdfOutcome<List<LoadedStationary>> = emptyList<LoadedStationary>().asSuccess()
        return pages.map { it.stationary }.flatten().toSet()
            .fold(empty) { acc, stationary ->
                when (acc) {
                    is Outcome.Failure<*, *> -> acc
                    is Outcome.Success -> load(stationary.template).map { loaded ->
                        acc.result + LoadedStationary(stationary, PdfReader(loaded))
                    }
                }
            }.map { list -> list.associateBy { it.stationary } }
    }
}
