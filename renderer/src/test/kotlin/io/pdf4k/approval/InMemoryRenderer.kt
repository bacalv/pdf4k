package io.pdf4k.approval

import io.pdf4k.domain.KeyName
import io.pdf4k.domain.Pdf
import io.pdf4k.renderer.*
import io.pdf4k.renderer.KeyProvider.Companion.toCertificateChain
import io.pdf4k.renderer.KeyProvider.Companion.toPrivateKey
import io.pdf4k.renderer.TempStreamFactory.Companion.inMemoryTempStreamFactory
import io.pdf4k.testing.AbstractPdfApproverTest
import org.junit.jupiter.api.Assertions.fail
import java.io.ByteArrayOutputStream
import java.io.OutputStream

object InMemoryRenderer {
    private val privateKey = AbstractPdfApproverTest::class.java.getResource("/certs/private-key.pem")
        ?.readText()?.let(::toPrivateKey)?.getOrNull() ?: fail("Could not load default private key")
    private val certificateChain = AbstractPdfApproverTest::class.java.getResource("/certs/cert.pem")
        ?.readText()?.let(::toCertificateChain)?.toList() ?: fail("Could not load default cert chain")

    val defaultKeyName = KeyName("default")
    val key = mutableMapOf(defaultKeyName to KeyProvider.Key(privateKey, certificateChain))
    private val keyProvider = object : KeyProvider {
        override fun lookup(keyName: KeyName) = key[keyName] ?: fail("Key not found: $keyName")
    }
    private val documentAssembler = DocumentAssembler(keyProvider)
    val renderer = PdfRenderer(ClasspathFontProvider, ClasspathStationaryLoader, inMemoryTempStreamFactory, documentAssembler)

    fun Pdf.render(outputStream: OutputStream = ByteArrayOutputStream()) = renderer.render(this, outputStream)
}