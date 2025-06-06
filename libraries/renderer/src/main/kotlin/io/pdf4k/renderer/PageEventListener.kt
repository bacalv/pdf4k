package io.pdf4k.renderer

import com.lowagie.text.Chunk
import com.lowagie.text.Document
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import io.pdf4k.domain.LoadedStationary
import io.pdf4k.domain.Section
import io.pdf4k.domain.Stationary
import java.util.concurrent.atomic.AtomicReference

class PageEventListener(private val context: RendererContext) : PdfPageEventHelper() {
    private val currentSection = AtomicReference<Section>()
    private var templatePageCount = 0
    private var currentBlockCount = 0

    val blocksUntilNextPage: Int get() = currentStationary().contentFlow.size - currentBlockCount

    fun setCurrentSection(section: Section) {
        if (currentBlockCount > 0 && currentSection.get() != null) {
            currentTemplate()?.let { context.stationaryByPage += it to currentBlockCount }
        }
        currentSection.set(section)
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
        currentTemplate()?.let { loadedStationary ->
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
            } ?: throw IllegalStateException(
                "Cannot find block #${currentBlockCount}" +
                        " for template '${loadedStationary.stationary.template}'"
            )
        } ?: throw IllegalStateException("Cannot find loaded stationary for template '${currentStationary().template}'")
    }

    private fun currentTemplate(): LoadedStationary? {
        return context.loadedStationary[currentStationary()]
    }

    private fun Stationary.getBlock(sequence: Int) = contentFlow.getOrNull(sequence)
        ?.let { blockName -> blocks[blockName] }

    fun close() {
        if (currentBlockCount > 0) {
            val stationary = currentStationary()
            context.nextPage(stationary, currentBlockCount)
        }
    }

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
        val stationary = currentStationary()
        if (currentBlockCount == 0) {
            nextPage()
        }
        if (currentBlockCount == stationary.contentFlow.size - 1) {
            context.nextPage(stationary, currentBlockCount + 1)
            context.drawBlocks(currentSection.get(), stationary)
            templatePageCount++
            currentBlockCount = 0
        } else {
            currentBlockCount++
        }
        setTemplate()
    }

    private fun currentStationary() = currentSection.get().stationary.elementOrLast(templatePageCount)

    private fun nextPage() {
        if (!context.mainDocument.isOpen) {
            context.mainDocument.open()
        } else {
            context.mainDocument.newPage()
        }
        context.mainDocument.add(Chunk(""))
    }

    companion object {
        fun <E> List<E>.elementOrLast(i: Int) = getOrNull(i) ?: last()
    }
}