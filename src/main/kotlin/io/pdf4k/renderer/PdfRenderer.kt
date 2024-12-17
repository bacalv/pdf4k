package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.FontFactory
import com.lowagie.text.FontFactoryImp
import com.lowagie.text.pdf.*
import io.pdf4k.domain.Page
import io.pdf4k.domain.Pdf
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
        val tempStream = ByteArrayOutputStream()
        val context = renderDocument(tempStream, loadedStationary)
        applyTemplates(tempStream, outputStream, context)
        loadedStationary.values.forEach { it.reader.close() }
    }

    private fun Pdf.renderDocument(tempStream: ByteArrayOutputStream, loadedStationary: Map<String, LoadedStationary>): RendererContext {
        val document = Document()
        val writer = PdfWriter.getInstance(document, tempStream)
        val context = RendererContext(document, writer, loadedStationary)
        val eventListener = PageEventListener(context)
        writer.pageEvent = eventListener
        context.pushStyle(style)
        pages.forEach {
            eventListener.setCurrentPageTemplate(it)
            it.render(context)
        }
        context.popStyle()
        document.close()
        return context
    }

    private fun applyTemplates(tempStream: ByteArrayOutputStream, outputStream: OutputStream, context: RendererContext) {
        val pdfReader = PdfReader(tempStream.toByteArray())
        val stamper = PdfStamper(pdfReader, outputStream)
        context.stationaryByPage.forEachIndexed { i, template ->
            val imported = stamper.getImportedPage(template.reader, template.stationary.templatePage)
            stamper.getUnderContent(i + 1)?.addTemplate(imported, 0f, 0f)
        }
        stamper.cleanMetadata()
        stamper.close()
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
                ColumnText(context.writer.directContent).let { columnText ->
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

        fun setCurrentPageTemplate(page: Page) {
            currentPageTemplate = page
            templatePageCount = 0
            setTemplate()
            if (!context.document.isOpen) {
                context.document.open()
            } else {
                context.document.newPage()
            }
        }

        private fun setTemplate() {
            val currentTemplate = currentPageTemplate.stationary.getOrNull(templatePageCount)
                ?: currentPageTemplate.stationary.last()
            context.loadedStationary[currentTemplate.template]?.let {
                context.document.setPageSize(it.pageSize)
                with(currentTemplate.margin) {
                    context.document.setMargins(left, right, top, bottom)
                }
            }
        }

        override fun onStartPage(writer: PdfWriter?, document: Document?) {
            val stationary = currentPageTemplate.stationary.getOrNull(templatePageCount)
                ?: currentPageTemplate.stationary.last()
            context.nextPage(stationary)
            drawBlocks(currentPageTemplate, stationary, context)
            templatePageCount++
            setTemplate()
        }
    }
}
