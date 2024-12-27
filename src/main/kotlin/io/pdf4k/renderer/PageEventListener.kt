package io.pdf4k.renderer

import com.lowagie.text.Chunk
import com.lowagie.text.Document
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import io.pdf4k.domain.Page
import io.pdf4k.domain.Stationary

class PageEventListener(private val context: RendererContext) : PdfPageEventHelper() {
    private lateinit var currentPageTemplate: Page
    private var templatePageCount = 0
    private var currentBlockCount = 0

    fun setCurrentPageTemplate(page: Page) {
        currentPageTemplate = page
        templatePageCount = 0
        currentBlockCount = 0
        setTemplate()
        if (!context.contentBlocksDocument.isOpen) {
            context.contentBlocksDocument.open()
        } else {
            context.contentBlocksDocument.newPage()
        }
    }

    private fun setTemplate() {
        val currentTemplate = currentPageTemplate.stationary.getOrNull(templatePageCount)
            ?: currentPageTemplate.stationary.last()
        context.loadedStationary[currentTemplate.template]?.let { loadedStationary ->
            context.mainDocument.setPageSize(loadedStationary.pageSize)
            with(currentTemplate.margin) {
                context.mainDocument.setMargins(left, right, top, bottom)
            }
            val currentBlock = loadedStationary.stationary.getBlock(currentBlockCount)
            if (currentBlock == null) {
                context.contentBlocksDocument.setPageSize(loadedStationary.pageSize)
                with(currentTemplate.margin) {
                    context.contentBlocksDocument.setMargins(left, right, top, bottom)
                }
            } else {
                context.contentBlocksDocument.setPageSize(Rectangle(currentBlock.w, currentBlock.h))
                context.contentBlocksDocument.setMargins(0f, 0f, 0f, 0f)
            }
        }
    }

    private fun Stationary.getBlock(sequence: Int) = contentFlow.getOrNull(sequence)?.let { blockName -> blocks[blockName] }

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
        val stationary = currentPageTemplate.stationary.getOrNull(templatePageCount)
            ?: currentPageTemplate.stationary.last()

        if (stationary.contentFlow.isEmpty()) {
            context.nextPage(stationary, 1)
            nextPage()
            context.drawBlocks(currentPageTemplate, stationary)
            templatePageCount++
            setTemplate()
        } else {
            if (currentBlockCount == 0) {
                nextPage()
            }
            if (currentBlockCount == stationary.contentFlow.size - 1) {
                context.nextPage(stationary, currentBlockCount)
                context.drawBlocks(currentPageTemplate, stationary)
                templatePageCount++
                currentBlockCount = 0
            } else {
                currentBlockCount++
            }
            setTemplate()
        }
    }

    private fun nextPage() {
        if (!context.mainDocument.isOpen) {
            context.mainDocument.open()
        } else {
            context.mainDocument.newPage()
        }
        context.mainDocument.add(Chunk(""))
    }
}