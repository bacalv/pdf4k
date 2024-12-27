package io.pdf4k.renderer

import com.lowagie.text.Chunk
import com.lowagie.text.Document
import com.lowagie.text.FontFactory
import com.lowagie.text.FontFactoryImp
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.*
import io.pdf4k.domain.Page
import io.pdf4k.domain.Pdf
import io.pdf4k.domain.PdfMetadata
import io.pdf4k.domain.Stationary
import io.pdf4k.renderer.ComponentRenderer.render
import io.pdf4k.renderer.PageRenderer.render
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.nio.file.FileSystems
import java.nio.file.Files
import kotlin.io.path.extension

object PdfRenderer {
    private const val fontResource = "/fonts"

    init {
        loadFonts()
    }

    fun Pdf.render(outputStream: OutputStream) {
        val loadedStationary = loadStationary(pages.map { it.stationary }.flatten())
        val mainDocumentStream = ByteArrayOutputStream()
        val contentBlocksDocumentStream = ByteArrayOutputStream()
        val context = renderDocument(mainDocumentStream, contentBlocksDocumentStream, loadedStationary)
        applyTemplates(mainDocumentStream, contentBlocksDocumentStream, outputStream, context)
        loadedStationary.values.forEach { it.reader.close() }
    }

    private fun Pdf.renderDocument(mainDocumentStream: ByteArrayOutputStream, contentBlocksDocumentStream: ByteArrayOutputStream, loadedStationary: Map<String, LoadedStationary>): RendererContext {
        val mainDocument = Document()
        val mainDocumentWriter = PdfWriter.getInstance(mainDocument, mainDocumentStream)
        mainDocument.setMetadata(metadata)
        val contentBlocksDocument = Document()
        val contentBlocksDocumentWriter = PdfWriter.getInstance(contentBlocksDocument, contentBlocksDocumentStream)
        val context = RendererContext(mainDocument, mainDocumentWriter, contentBlocksDocument, contentBlocksDocumentWriter, loadedStationary)
        val eventListener = PageEventListener(context)
        contentBlocksDocumentWriter.pageEvent = eventListener
        context.pushStyle(style)
        pages.forEach {
            eventListener.setCurrentPageTemplate(it)
            it.render(context)
        }
        context.popStyle()
        mainDocument.close()
        contentBlocksDocumentWriter.pageEvent = null
        contentBlocksDocument.close()
        return context
    }

    private fun Document.setMetadata(metadata: PdfMetadata) {
        metadata.title?.let { addTitle(it) }
        metadata.author?.let { addAuthor(it) }
        metadata.subject?.let { addSubject(it) }
        metadata.keywords?.let { addKeywords(it) }
        metadata.creator?.let { addCreator(it) }
        metadata.producer?.let { addProducer(it) }
    }

    private fun applyTemplates(
        mainDocumentStream: ByteArrayOutputStream,
        contentBlocksDocumentStream: ByteArrayOutputStream,
        outputStream: OutputStream,
        context: RendererContext
    ) {
        val mainDocumentReader = PdfReader(mainDocumentStream.toByteArray())
        val contentReader = PdfReader(contentBlocksDocumentStream.toByteArray())
        val stamper = PdfStamper(mainDocumentReader, outputStream)
        stampPageTemplates(stamper, contentReader, context)
        stamper.close()
    }

    private fun stampPageTemplates(stamper: PdfStamper, contentReader: PdfReader, context: RendererContext) {
        var contentPage = 1
        context.stationaryByPage.forEachIndexed { pageNumber, (template, blocksFilled) ->
            val imported = stamper.getImportedPage(template.reader, template.stationary.templatePage)
            stamper.getUnderContent(pageNumber + 1)?.addTemplate(imported, 0f, 0f)
            if (template.stationary.contentFlow.isEmpty()) {
                val importedBlock = stamper.getImportedPage(contentReader, contentPage++)
                stamper.getOverContent(pageNumber + 1)?.addTemplate(importedBlock, 0f, 0f)
            }
            template.stationary.contentFlow.forEachIndexed { blockNumber, blockName ->
                if (blockNumber <= blocksFilled) {
                    template.stationary.blocks[blockName]?.let { block ->
                        val importedBlock = stamper.getImportedPage(contentReader, contentPage++)
                        stamper.getOverContent(pageNumber + 1)?.addTemplate(importedBlock, block.x, block.y)
                    }
                }
            }
        }
    }

    private fun loadStationary(stationaryList: List<Stationary>): Map<String, LoadedStationary> =
        stationaryList.associate { stationary ->
            val stream = PdfRenderer::class.java.getResourceAsStream("/stationary/${stationary.template}.pdf")
                ?: throw IllegalStateException("Can't find stationary: ${stationary.template}")
            val reader = PdfReader(stream)
            val pageSize = reader.getPageSize(stationary.templatePage)
            stationary.template to LoadedStationary(stationary, pageSize, reader)
        }

    private fun drawBlocks(page: Page, stationary: Stationary, context: RendererContext) {
        page.blockContent.forEach { (blockName, content) ->
            stationary.blocks[blockName]?.let { block ->
                ColumnText(context.mainDocumentWriter.directContent).let { columnText ->
                    columnText.setSimpleColumn(block.x, block.y + block.h, block.x + block.w, block.y)
                    content.children.render(context).forEach { columnText.addElement(it) }
                    columnText.go()
                }
            }
        }
    }

    private fun loadFonts() {
        FontFactory.defaultEmbedding = BaseFont.EMBEDDED
        FontFactory.defaultEncoding = BaseFont.IDENTITY_H
        FontFactory.setFontImp(FontFactoryImp().also { it.defaultEmbedding = BaseFont.EMBEDDED })
        val uri = PdfRenderer::class.java.getResource(fontResource)?.toURI()
            ?: throw IllegalStateException("Failed to read font directory")
        val fontsRegistered = if (uri.scheme != "file") {
            FileSystems.newFileSystem(uri, emptyMap<String, Any>()).use { fileSystem ->
                Files.walk(fileSystem.getPath(fontResource)).filter { it.extension == "ttf" }
                    .map { FontFactory.register(it.toUri().toString()) }.count()
            }
        } else {
            Files.walk(File(uri.path).toPath()).filter { it.extension == "ttf" }
                .map { FontFactory.register(it.toString()) }.count()
        }
        if (fontsRegistered == 0L) throw IllegalStateException("No fonts found")
    }

    private class PageEventListener(val context: RendererContext) : PdfPageEventHelper() {
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

        fun Stationary.getBlock(sequence: Int) = contentFlow.getOrNull(sequence)?.let { blockName -> blocks[blockName] }
        
        override fun onStartPage(writer: PdfWriter?, document: Document?) {
            val stationary = currentPageTemplate.stationary.getOrNull(templatePageCount)
                ?: currentPageTemplate.stationary.last()

            if (stationary.contentFlow.isEmpty()) {
                context.nextPage(stationary, 1)
                nextPage()
                drawBlocks(currentPageTemplate, stationary, context)
                templatePageCount++
                setTemplate()
            } else {
                if (currentBlockCount == 0) {
                    nextPage()
                }
                if (currentBlockCount == stationary.contentFlow.size - 1) {
                    context.nextPage(stationary, currentBlockCount)
                    drawBlocks(currentPageTemplate, stationary, context)
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
                context.mainDocument.add(Chunk(""))
            } else {
                if (context.mainDocument.newPage()) {
                    context.mainDocument.add(Chunk(""))
                }
            }
        }
    }
}
