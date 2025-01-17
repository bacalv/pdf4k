package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.FontFactory
import com.lowagie.text.FontFactoryImp
import com.lowagie.text.pdf.*
import io.pdf4k.domain.Pdf
import io.pdf4k.domain.PdfMetadata
import io.pdf4k.domain.Stationary
import io.pdf4k.renderer.PageRenderer.render
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.nio.file.FileSystems
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.extension

object PdfRenderer {
    private const val FONT_RESOURCE = "/fonts"

    init {
        loadFonts()
    }

    fun Pdf.render(outputStream: OutputStream) {
        val loadedStationary = loadStationary(pages.map { it.stationary }.flatten())
        val mainDocumentStream = ByteArrayOutputStream()
        val contentBlocksDocumentStream = ByteArrayOutputStream()
        val context = renderDocument(mainDocumentStream, contentBlocksDocumentStream, loadedStationary)
        Files.write(Path("./blocks.pdf"), contentBlocksDocumentStream.toByteArray())
        applyTemplates(mainDocumentStream, contentBlocksDocumentStream, outputStream, context)
        loadedStationary.values.forEach { it.reader.close() }
    }

    private fun Pdf.renderDocument(mainDocumentStream: ByteArrayOutputStream, contentBlocksDocumentStream: ByteArrayOutputStream, loadedStationary: Map<Stationary, LoadedStationary>): RendererContext {
        val mainDocument = Document()
        PdfWriter.getInstance(mainDocument, mainDocumentStream)
        mainDocument.setMetadata(metadata)
        val contentBlocksDocument = Document()
        val contentBlocksDocumentWriter = PdfWriter.getInstance(contentBlocksDocument, contentBlocksDocumentStream)
        val context = RendererContext(
            mainDocument,
            contentBlocksDocument,
            contentBlocksDocumentWriter,
            loadedStationary
        )
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
        val stamper = PdfStamperAccessor(mainDocumentReader, outputStream)
        stampPageTemplates(stamper, contentReader, context)
        PdfWriterAccessor(stamper.writer).writeNamedDestinations()
        stamper.close()
    }

    private fun stampPageTemplates(stamper: PdfStamperAccessor, contentReader: PdfReader, context: RendererContext) {
        var contentPage = 1
        val pageNumberMap = (1..stamper.reader.numberOfPages).map { contentReader.getPageOrigRef(it).number to it }.toMap()
        val blockPageMapping = context.stationaryByPage.mapIndexed { pageNumber, (_, blocksFilled) ->
            pageNumber to blocksFilled + 1
        }.map { (pageNumber, blocksFilled) ->
            (1..blocksFilled).map { pageNumber }
        }.flatten()
        val namedDestinations = contentReader.namedDestination

        context.stationaryByPage.forEachIndexed { pageNumber, (template, blocksFilled) ->
            val imported = stamper.getImportedPage(template.reader, template.stationary.templatePage)
            stamper.getUnderContent(pageNumber + 1)?.addTemplate(imported, 0f, 0f)
            template.stationary.contentFlow.forEachIndexed { blockNumber, _ ->
                if (blockNumber <= blocksFilled) {
                    val importedBlock = stamper.getImportedPage(contentReader, contentPage)
                    stamper.getOverContent(pageNumber + 1)?.addTemplate(importedBlock, 0f, 0f)
                    copyLinks(contentReader, contentPage, stamper, pageNumber, pageNumberMap, blockPageMapping, namedDestinations)
                    contentPage++
                }
            }
        }
//
//        contentReader.namedDestination.map { (name, obj) ->
//            val array = obj as PdfArray
//            val pageRef = array.elements[0] as PRIndirectReference
//            val realPage = pageNumberMap[pageRef.number] ?: 1
//            val x = array.elements[2] as PdfNumber
//            val y = array.elements[3] as PdfNumber
//            val destination = PdfDestination(PdfDestination.XYZ, x.floatValue(), y.floatValue(),0f)
//            stamper.writer.addNamedDestination(name.toString(), realPage, destination)
//        }
    }

    private fun copyLinks(
        contentReader: PdfReader,
        contentPage: Int,
        stamper: PdfStamperAccessor,
        pageNumber: Int,
        pageNumberMap: Map<Int, Int>,
        blockPageMapping: List<Int>,
        namedDestinations: Map<Any, PdfObject>
    ) {
        contentReader.getLinks(contentPage).map(::PdfLinkAccessor).forEach { accessor ->
            if (accessor.isLocal()) {
                val array = contentReader.getPdfObject(accessor.getReference().number) as PdfArray
                val page = array.elements[0] as PRIndirectReference
                val destinationBlockNumber = pageNumberMap[page.number]
                val destinationPage = blockPageMapping[(destinationBlockNumber ?: 1) - 1]
                val destinationPageRef = stamper.writer.getPageReference(destinationPage + 1)
                val annotation = accessor.link.createAnnotation(stamper.writer)
                annotation.setPage(destinationPage + 1)
                val newArray = PdfArray(listOf(destinationPageRef, array.elements[1], array.elements[2], array.elements[3], array.elements[4]))
                val ref = stamper.writer.addToBody(newArray)
                ((annotation as PdfDictionary).get(PdfName.A) as PdfDictionary).put(PdfName.D, ref.indirectReference)
                stamper.addAnnotation(annotation, pageNumber + 1)
                val name = namedDestinations.filter { it.value == array }.map { it.key }.first()
                PdfWriterAccessor(stamper.writer).addNamedDestination(name, ref)
            } else {
                accessor.link.createAnnotation(stamper.writer).also {
                    stamper.addAnnotation(it, pageNumber + 1)
                }
            }
        }
    }

    private fun loadStationary(stationaryList: List<Stationary>): Map<Stationary, LoadedStationary> =
        stationaryList.associateWith { stationary ->
            val stream = PdfRenderer::class.java.getResourceAsStream("/stationary/${stationary.template}.pdf")
                ?: throw IllegalStateException("Can't find stationary: ${stationary.template}")
            val reader = PdfReader(stream)
            LoadedStationary(stationary, reader)
        }

    private fun loadFonts() {
        FontFactory.defaultEmbedding = BaseFont.EMBEDDED
        FontFactory.defaultEncoding = BaseFont.IDENTITY_H
        FontFactory.setFontImp(FontFactoryImp().also { it.defaultEmbedding = BaseFont.EMBEDDED })
        val uri = PdfRenderer::class.java.getResource(FONT_RESOURCE)?.toURI()
            ?: throw IllegalStateException("Failed to read font directory")
        val fontsRegistered = if (uri.scheme != "file") {
            FileSystems.newFileSystem(uri, emptyMap<String, Any>()).use { fileSystem ->
                Files.walk(fileSystem.getPath(FONT_RESOURCE)).filter { it.extension == "ttf" }
                    .map { FontFactory.register(it.toUri().toString()) }.count()
            }
        } else {
            Files.walk(File(uri.path).toPath()).filter { it.extension == "ttf" }
                .map { FontFactory.register(it.toString()) }.count()
        }
        if (fontsRegistered == 0L) throw IllegalStateException("No fonts found")
    }
}