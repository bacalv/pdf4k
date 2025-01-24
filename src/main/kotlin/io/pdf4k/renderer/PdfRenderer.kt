package io.pdf4k.renderer

import com.lowagie.text.Document
import com.lowagie.text.FontFactory
import com.lowagie.text.FontFactoryImp
import com.lowagie.text.pdf.*
import io.pdf4k.domain.*
import io.pdf4k.domain.PdfPermissions.PdfPermission.*
import io.pdf4k.renderer.PageRenderer.render
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.*
import kotlin.io.path.extension

object PdfRenderer {
    private const val FONT_RESOURCE = "/fonts"

    init {
        loadFonts()
    }

    fun Pdf.render(outputStream: OutputStream, keyProvider: KeyProvider) {
        val loadedStationary = loadStationary(pages.map { it.stationary }.flatten())
        val mainDocumentStream = ByteArrayOutputStream()
        val contentBlocksDocumentStream = ByteArrayOutputStream()
        val context = paginateDocument(mainDocumentStream, contentBlocksDocumentStream, loadedStationary)
        val contentReader = PdfReader(contentBlocksDocumentStream.toByteArray())
        context.copyNamedDestinations(contentReader)
        context.mainDocument.close()
        val mainContentBytes = mainDocumentStream.toByteArray()
//        Files.write(Path("./main.pdf"), mainContentBytes)
//        Files.write(Path("./blocks.pdf"), contentBlocksDocumentStream.toByteArray())
        applyTemplates(PdfReader(mainContentBytes), contentReader, outputStream, context, keyProvider)
        loadedStationary.values.forEach { it.reader.close() }
    }

    private fun Pdf.paginateDocument(
        mainDocumentStream: ByteArrayOutputStream,
        contentBlocksDocumentStream: ByteArrayOutputStream,
        loadedStationary: Map<Stationary, LoadedStationary>
    ): RendererContext {
        val mainDocument = Document()
        val mainDocumentWriter = PdfWriter.getInstance(mainDocument, mainDocumentStream)
        mainDocument.setMetadata(metadata)
        val contentBlocksDocument = Document()
        val contentBlocksDocumentWriter = PdfWriter.getInstance(contentBlocksDocument, contentBlocksDocumentStream)
        val context = RendererContext(
            mainDocument,
            mainDocumentWriter,
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
        contentBlocksDocumentWriter.pageEvent = null
        eventListener.close()
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

    private fun RendererContext.copyNamedDestinations(contentReader: PdfReader) {
        val blockNumbers =
            (1..contentReader.numberOfPages).map { page -> contentReader.getPageOrigRef(page).number to page }.toMap()
        contentReader.namedDestination.forEach { (name, obj) ->
            (obj as? PdfArray)?.elements?.let { array ->
                val blockNumber = blockNumbers[(array[0] as PdfIndirectReference).number]
                    ?: throw IllegalStateException("Page not found ${array[0]}")
                val translatedPage = findPageForBlockNumber(blockNumber)
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

    private fun RendererContext.findPageForBlockNumber(blockNumber: Int): Int {
        var sum = 0
        stationaryByPage.forEachIndexed { index, (_, blocksFilled) ->
            sum += blocksFilled
            if (sum >= blockNumber) return index + 1
        }
        throw IllegalStateException("Can't find page for block $blockNumber")
    }

    private fun Pdf.applyTemplates(
        mainDocumentReader: PdfReader,
        contentReader: PdfReader,
        outputStream: OutputStream,
        context: RendererContext,
        keyProvider: KeyProvider
    ) {
        val stamper = if (signature != null) {
            signature.createSigningStamper(keyProvider, mainDocumentReader, outputStream)
        } else {
            PdfStamper(mainDocumentReader, outputStream, '\u0000')
        }
        if (permissions != null) {
            stamper.setEncryption(
                permissions.userPassword.toByteArray(),
                permissions.ownerPassword.toByteArray(),
                permissions.toInt(),
                PdfWriter.ENCRYPTION_AES_256_V3
            )
        }
        stampPageTemplates(stamper, contentReader, context)
        stamper.close()
    }

    private fun PdfPermissions.toInt() =
        permissions.fold(0) { acc, e ->
            when (e) {
                Print -> PdfWriter.ALLOW_PRINTING
                ModifyContents -> PdfWriter.ALLOW_MODIFY_CONTENTS
                Copy -> PdfWriter.ALLOW_COPY
                ModifyAnnotations -> PdfWriter.ALLOW_MODIFY_ANNOTATIONS
                FillIn -> PdfWriter.ALLOW_FILL_IN
                ScreenReaders -> PdfWriter.ALLOW_SCREENREADERS
                Assembly -> PdfWriter.ALLOW_ASSEMBLY
                DegradedPrint -> PdfWriter.ALLOW_DEGRADED_PRINTING
            } or acc
        }

    private fun Signature.createSigningStamper(
        keyProvider: KeyProvider,
        mainDocumentReader: PdfReader,
        outputStream: OutputStream
    ): PdfStamper {
        val keys = keyProvider.lookup(keyName)
        return PdfStamper.createSignature(mainDocumentReader, outputStream, '\u0000').also {
            it.signatureAppearance.apply {
                setCrypto(
                    keys.privateKey,
                    keys.certificateChain.toTypedArray(),
                    emptyArray(),
                    PdfSignatureAppearance.WINCER_SIGNED
                )
                reason = this@createSigningStamper.reason
                location = this@createSigningStamper.location
                contact = this@createSigningStamper.contact
                signDate =
                    Calendar.getInstance().also { it.time = Date.from(this@createSigningStamper.signDate.toInstant()) }
            }
        }
    }

    private fun stampPageTemplates(stamper: PdfStamper, contentReader: PdfReader, context: RendererContext) {
        var contentPage = 1
        val pageNumberMap =
            (1..stamper.reader.numberOfPages).map { contentReader.getPageOrigRef(it).number to it }.toMap()
        val blockPageMapping = context.stationaryByPage.mapIndexed { pageNumber, (_, blocksFilled) ->
            pageNumber to blocksFilled
        }.map { (pageNumber, blocksFilled) ->
            (0 until blocksFilled).map { pageNumber }
        }.flatten()
        val namedDestinations = mutableMapOf<String, PdfIndirectReference>()
        context.stationaryByPage.forEachIndexed { pageNumber, (template, blocksFilled) ->
            val imported = stamper.getImportedPage(template.reader, template.stationary.templatePage)
            stamper.getUnderContent(pageNumber + 1)?.addTemplate(imported, 0f, 0f)
            template.stationary.contentFlow.forEachIndexed { blockNumber, _ ->
                if (blockNumber < blocksFilled) {
                    val importedBlock = stamper.getImportedPage(contentReader, contentPage)
                    stamper.getOverContent(pageNumber + 1)?.addTemplate(importedBlock, 0f, 0f)
                    copyLinks(
                        contentReader,
                        contentPage,
                        stamper,
                        pageNumber,
                        pageNumberMap,
                        blockPageMapping,
                        namedDestinations
                    )
                    contentPage++
                }
            }
        }
    }

    private fun copyLinks(
        contentReader: PdfReader,
        contentPage: Int,
        stamper: PdfStamper,
        pageNumber: Int,
        pageNumberMap: Map<Int, Int>,
        blockPageMapping: List<Int>,
        namedDestinations: MutableMap<String, PdfIndirectReference>
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
                val name = contentReader.namedDestination.filter { it.value == array }.map { it.key }.first().toString()
                val destination = namedDestinations.getOrPut(name) {
                    val newArray = PdfArray(
                        listOf(
                            destinationPageRef,
                            array.elements[1],
                            array.elements[2],
                            array.elements[3],
                            array.elements[4]
                        )
                    )
                    val ref = stamper.writer.addToBody(newArray)
                    ref.indirectReference
                }
                (annotation.get(PdfName.A) as PdfDictionary).put(PdfName.D, destination)
                stamper.addAnnotation(annotation, pageNumber + 1)
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
