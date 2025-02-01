package io.pdf4k.renderer

import com.lowagie.text.pdf.*
import io.pdf4k.domain.Pdf
import io.pdf4k.domain.PdfPermissions
import io.pdf4k.domain.PdfPermissions.PdfPermission.*
import io.pdf4k.domain.Signature
import io.pdf4k.provider.KeyProvider
import java.io.OutputStream
import java.util.*

class DocumentAssembler(private val keyProvider: KeyProvider) {
    fun assemble(
        pdf: Pdf,
        mainDocumentReader: PdfReader,
        contentReader: PdfReader,
        outputStream: OutputStream,
        context: RendererContext
    ) = with(pdf) {
        val signature = signature
        val stamper = if (signature == null) {
            PdfStamper(mainDocumentReader, outputStream, '\u0000')
        } else {
            signature.createSigningStamper(keyProvider, mainDocumentReader, outputStream)
        }
        permissions?.let {
            stamper.setEncryption(
                it.userPassword.toByteArray(),
                it.ownerPassword.toByteArray(),
                it.toInt(),
                PdfWriter.ENCRYPTION_AES_256_V3
            )
        }
        stamper.stampPageTemplates(contentReader, context)
        val info: PdfDictionary = stamper.reader.trailer.getAsDict(PdfName.INFO)
        metadata.customProperties.forEach {
            info.put(PdfName(it.key),  PdfString(it.value))
        }
        stamper.close()
    }

    private fun Signature.createSigningStamper(
        keyProvider: KeyProvider,
        mainDocumentReader: PdfReader,
        outputStream: OutputStream
    ) = keyProvider.lookup(keyName).let { key ->
        PdfStamper.createSignature(mainDocumentReader, outputStream, '\u0000').also { stamper ->
            stamper.signatureAppearance.also {
                it.setCrypto(
                    key.privateKey,
                    key.certificateChain.toTypedArray(),
                    emptyArray(),
                    PdfSignatureAppearance.WINCER_SIGNED
                )
                it.reason = reason
                it.location = location
                it.contact = contact
                it.signDate = Calendar.getInstance().also { it.time = Date.from(this.signDate.toInstant()) }
            }
        }
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

    private fun PdfStamper.stampPageTemplates(contentReader: PdfReader, context: RendererContext) {
        var contentPage = 1
        val pageNumberMap =
            (1..reader.numberOfPages).map { contentReader.getPageOrigRef(it).number to it }.toMap()
        val blockPageMapping = context.getBlockPageMapping()
        val namedDestinations = mutableMapOf<String, PdfIndirectReference>()
        context.stationaryByPage.forEachIndexed { pageNumber, (template, blocksFilled) ->
            val imported = getImportedPage(template.reader, template.stationary.templatePage)
            getUnderContent(pageNumber + 1).addTemplate(imported, 0f, 0f)
            template.stationary.contentFlow.forEachIndexed { blockNumber, _ ->
                if (blockNumber < blocksFilled) {
                    val importedBlock = getImportedPage(contentReader, contentPage)
                    getOverContent(pageNumber + 1).addTemplate(importedBlock, 0f, 0f)
                    this.copyLinks(
                        contentReader,
                        contentPage,
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

    private fun PdfStamper.copyLinks(
        contentReader: PdfReader,
        contentPage: Int,
        pageNumber: Int,
        pageNumberMap: Map<Int, Int>,
        blockPageMapping: List<Int>,
        namedDestinations: MutableMap<String, PdfIndirectReference>
    ) {
        contentReader.getLinks(contentPage).map(::PdfLinkAccessor).forEach { accessor ->
            if (accessor.isLocal()) {
                val array = contentReader.getPdfObject(accessor.getReference().number) as PdfArray
                val page = array.elements[0] as PRIndirectReference
                val destinationBlockNumber = pageNumberMap.getOrDefault(page.number, 1)
                val destinationPage = blockPageMapping[(destinationBlockNumber) - 1]
                val destinationPageRef = writer.getPageReference(destinationPage + 1)
                val annotation = accessor.link.createAnnotation(writer)
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
                    writer.addToBody(newArray).indirectReference
                }
                (annotation.get(PdfName.A) as PdfDictionary).put(PdfName.D, destination)
                addAnnotation(annotation, pageNumber + 1)
            } else {
                accessor.link.createAnnotation(writer).also {
                    addAnnotation(it, pageNumber + 1)
                }
            }
        }
    }
}
