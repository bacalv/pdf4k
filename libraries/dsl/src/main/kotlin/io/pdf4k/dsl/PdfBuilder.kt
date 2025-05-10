package io.pdf4k.dsl

import io.pdf4k.domain.*
import io.pdf4k.domain.PdfPermissions.PdfPermission
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import java.time.ZoneId
import java.time.ZonedDateTime

@PdfDsl
class PdfBuilder(val style: StyleAttributes?) {
    private val sections = mutableListOf<Section>()
    private val metadataBuilder = MetadataBuilder()
    private var signature: Signature? = null
    private var permissions: PdfPermissions? = null

    fun section(
        style: StyleAttributes? = null,
        stationary: Stationary = BlankA4Portrait,
        vararg continuation: Stationary = arrayOf(),
        block: SectionBuilder.() -> Unit = {}
    ) {
        val builder = SectionBuilder(style, listOf(stationary) + continuation.toList())
        builder.block()
        sections += builder.build()
    }

    fun metadata(block: MetadataBuilder.() -> Unit) {
        metadataBuilder.block()
    }

    fun sign(
        keyName: KeyName,
        reason: String,
        location: String,
        contact: String,
        signDate: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
    ) {
        signature = Signature(keyName, reason, location, contact, signDate)
    }

    private fun build(): Pdf {
        return Pdf(style, sections, metadataBuilder.build(), signature, permissions)
    }

    fun encrypt(userPassword: String, ownerPassword: String, vararg permission: PdfPermission) {
        permissions = PdfPermissions(userPassword, ownerPassword, permission.toSet())
    }

    companion object {
        fun pdf(style: StyleAttributes? = null, block: PdfBuilder.() -> Unit): Pdf {
            val builder = PdfBuilder(style)
            builder.block()
            return builder.build()
        }

        fun content(block: SectionContentBuilder.() -> Unit) = pdf { section { content { block() } } }
    }
}