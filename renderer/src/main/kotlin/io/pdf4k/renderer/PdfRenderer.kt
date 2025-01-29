package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.pdf.*
import io.pdf4k.domain.Pdf
import io.pdf4k.domain.PdfMetadata
import io.pdf4k.domain.Stationary
import io.pdf4k.renderer.PageRenderer.render
import java.io.OutputStream

class PdfRenderer(
    private val fontProvider: FontProvider,
    private val stationaryLoader: StationaryLoader,
    private val tempStreamFactory: TempStreamFactory,
    private val documentAssembler: DocumentAssembler
) {
    fun render(pdf: Pdf, outputStream: OutputStream) = with(pdf) {
        val mainDocumentStream = tempStreamFactory.createTempOutputStream()
        val contentBlocksDocumentStream = tempStreamFactory.createTempOutputStream()
        stationaryLoader.loadStationary(pages.map { it.stationary }.flatten()).map { loadedStationary ->
            createContext(
                mainDocumentStream.outputStream,
                contentBlocksDocumentStream.outputStream,
                loadedStationary
            )
        }.map { context ->
            paginateDocument(context)
            contentBlocksDocumentStream.outputStream.close()
            val contentReader = PdfReader(contentBlocksDocumentStream.read())
            context.copyNamedDestinations(contentReader)
            context.mainDocument.setMetadata(metadata)
            context.mainDocument.close()
            mainDocumentStream.outputStream.close()
            documentAssembler.assemble(pdf, PdfReader(mainDocumentStream.read()), contentReader, outputStream, context)
            context.loadedStationary.values.forEach { it.reader.close() }
        }.onFailure {
            mainDocumentStream.outputStream.close()
            contentBlocksDocumentStream.outputStream.close()
        }
    }

    private fun createContext(
        mainDocumentStream: OutputStream,
        contentBlocksDocumentStream: OutputStream,
        loadedStationary: Map<Stationary, LoadedStationary>
    ) = Document().let { mainDocument ->
        Document().let { contentBlocksDocument ->
            RendererContext(
                mainDocument = mainDocument,
                mainDocumentWriter = PdfWriter.getInstance(mainDocument, mainDocumentStream),
                contentBlocksDocument = contentBlocksDocument,
                contentBlocksDocumentWriter = PdfWriter.getInstance(contentBlocksDocument, contentBlocksDocumentStream),
                loadedStationary = loadedStationary,
                fontProvider = fontProvider
            )
        }
    }

    private fun Pdf.paginateDocument(context: RendererContext) = with(context) {
        val eventListener = PageEventListener(context)
        contentBlocksDocumentWriter.pageEvent = eventListener
        context.pushStyle(style)
        pages.forEach {
            eventListener.setCurrentPageTemplate(it)
            it.render(context)
        }
        context.popStyle()
        contentBlocksDocumentWriter.pageEvent = null
        eventListener.close()
        contentBlocksDocument.close()
    }

    private fun Document.setMetadata(metadata: PdfMetadata) {
        metadata.title?.let { addTitle(it) }
        metadata.author?.let { addAuthor(it) }
        metadata.subject?.let { addSubject(it) }
        metadata.keywords?.let { addKeywords(it) }
        metadata.creator?.let { addCreator(it) }
        metadata.producer?.let { addProducer(it) }
    }

    private fun RendererContext.copyNamedDestinations(contentReader: PdfReader) {
        val blockNumbers = (1..contentReader.numberOfPages).associateBy { page ->
            contentReader.getPageOrigRef(page).number
        }
        val blockPageMapping = getBlockPageMapping()
        contentReader.namedDestination.forEach { (name, obj) ->
            (obj as PdfArray).elements.let { array ->
                val blockNumber = blockNumbers.getOrDefault((array[0] as PdfIndirectReference).number, 0)
                val translatedPage = blockPageMapping[blockNumber - 1] + 1
                val destination = PdfDestination(
                    0,
                    (array[2] as PdfNumber).floatValue(),
                    (array[3] as PdfNumber).floatValue(),
                    (array[4] as PdfNumber).floatValue()
                )
                mainDocumentWriter.addNamedDestination(name.toString(), translatedPage, destination)
            }
        }
    }
}
