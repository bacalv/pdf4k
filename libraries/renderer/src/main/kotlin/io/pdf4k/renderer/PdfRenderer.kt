package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.pdf.*
import io.pdf4k.domain.*
import io.pdf4k.domain.PdfError.RenderingError
import io.pdf4k.provider.ResourceLocators
import io.pdf4k.provider.StationaryLoader.loadStationary
import io.pdf4k.provider.TempStreamFactory
import io.pdf4k.renderer.PageRenderer.render
import java.io.OutputStream

class PdfRenderer(
    private val resourceLocators: ResourceLocators,
    private val tempStreamFactory: TempStreamFactory,
    private val documentAssembler: DocumentAssembler
) {
    fun render(pdf: Pdf, outputStream: OutputStream) = with(pdf) {
        tempStreamFactory.createTempOutputStream().use { mainDocumentStream ->
            tempStreamFactory.createTempOutputStream().use { contentBlocksDocumentStream ->
                resourceLocators.stationaryResourceLocator.loadStationary(pages).let { loadedStationary ->
                    createContext(
                        mainDocumentStream.outputStream,
                        contentBlocksDocumentStream.outputStream,
                        loadedStationary
                    )
                }.let { context ->
                    paginateDocument(context)
                    contentBlocksDocumentStream.outputStream.close()
                    PdfReader(contentBlocksDocumentStream.read()).let { contentReader ->
                        context.copyNamedDestinations(contentReader)
                        context.mainDocument.setMetadata(metadata)
                        context.mainDocument.close()
                        mainDocumentStream.outputStream.close()
                        documentAssembler.assemble(
                            pdf,
                            PdfReader(mainDocumentStream.read()),
                            contentReader,
                            outputStream,
                            context
                        )
                        context.loadedStationary.values.forEach { it.reader.close() }
                    }
                }
            }
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
                resourceLocators = resourceLocators
            )
        }
    }

    private fun Pdf.paginateDocument(context: RendererContext) = with(context) {
        try {
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
        } catch (e: PdfError) {
            throw e
        } catch (e: Exception) {
            throw RenderingError(e)
        }
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
