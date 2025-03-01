package io.pdf4k.renderer

import com.lowagie.text.pdf.PdfPTable
import kotlin.math.max

class ListTable(private val spacing: Float = 4f) : PdfPTable(2) {
    private var firstColumnWidth: Float = 0f

    override fun calculateWidths() {
        if (totalWidth > 0) {
            absoluteWidths[0] = firstColumnWidth + spacing
            absoluteWidths[1] = totalWidth - firstColumnWidth
        } else super.calculateWidths()
    }

    fun setFirstColumnWidth(width: Float) {
        firstColumnWidth = max(width, firstColumnWidth)
    }
}