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
        context.loadedStationary[currentTemplate]?.let { loadedStationary ->
            val pageSize = Rectangle(loadedStationary.stationary.width, loadedStationary.stationary.height)
            context.mainDocument.setPageSize(pageSize)
            context.mainDocument.setMargins(0f, 0f, 0f, 0f)
            context.contentBlocksDocument.setPageSize(pageSize)
            loadedStationary.stationary.getBlock(currentBlockCount)?.let { currentBlock ->
                context.contentBlocksDocument.setMargins(
                    currentBlock.x,
                    pageSize.width - (currentBlock.x + currentBlock.w),
                    pageSize.height - (currentBlock.y + currentBlock.h),
                    currentBlock.y
                )
            }
        }
    }

    private fun Stationary.getBlock(sequence: Int) = contentFlow[sequence].let { blockName -> blocks[blockName] }

    fun close() {
        if (currentBlockCount > 0) {
            val stationary = currentPageTemplate.stationary.getOrElse(templatePageCount) {
                currentPageTemplate.stationary.last()
            }
            context.nextPage(stationary, currentBlockCount)
        }
    }

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
        val stationary = currentPageTemplate.stationary.getOrNull(templatePageCount)
            ?: currentPageTemplate.stationary.last()
        if (currentBlockCount == 0) {
            nextPage()
        }
        if (currentBlockCount == stationary.contentFlow.size - 1) {
            context.nextPage(stationary, currentBlockCount + 1)
            context.drawBlocks(currentPageTemplate, stationary)
            templatePageCount++
            currentBlockCount = 0
        } else {
            currentBlockCount++
        }
        setTemplate()
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