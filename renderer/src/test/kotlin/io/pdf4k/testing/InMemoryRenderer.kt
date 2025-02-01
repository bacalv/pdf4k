package io.pdf4k.testing

import io.pdf4k.domain.KeyName
import io.pdf4k.domain.Pdf
import io.pdf4k.provider.KeyProvider
import io.pdf4k.provider.KeyProvider.Companion.toCertificateChain
import io.pdf4k.provider.KeyProvider.Companion.toPrivateKey
import io.pdf4k.provider.ResourceLocators
import io.pdf4k.provider.UriResourceLoader.Companion.defaultResourceLoader
import io.pdf4k.renderer.ClasspathFontProvider
import io.pdf4k.renderer.ClasspathStationaryLoader
import io.pdf4k.renderer.DocumentAssembler
import io.pdf4k.renderer.PdfRenderer
import io.pdf4k.renderer.TempStreamFactory.Companion.inMemoryTempStreamFactory
import org.junit.jupiter.api.Assertions.fail
import java.io.ByteArrayOutputStream
import java.io.OutputStream

object InMemoryRenderer {
    private val privateKey = AbstractPdfRendererTest::class.java.getResource("/certs/private-key.pem")
        ?.readText()?.let(::toPrivateKey)?.getOrNull() ?: fail("Could not load default private key")
    private val certificateChain = AbstractPdfRendererTest::class.java.getResource("/certs/cert.pem")
        ?.readText()?.let(::toCertificateChain)?.toList() ?: fail("Could not load default cert chain")

    val defaultKeyName = KeyName("default")
    val key = mutableMapOf(defaultKeyName to KeyProvider.Key(privateKey, certificateChain))
    private val keyProvider = object : KeyProvider {
        override fun lookup(keyName: KeyName) = key[keyName] ?: fail("Key not found: $keyName")
    }
    private val documentAssembler = DocumentAssembler(keyProvider)
    private val resourceLocators =
        ResourceLocators(defaultResourceLoader, emptyMap(), ClasspathFontProvider, ClasspathStationaryLoader)
    val renderer = PdfRenderer(resourceLocators, inMemoryTempStreamFactory, documentAssembler)

    fun Pdf.render(outputStream: OutputStream = ByteArrayOutputStream()) = renderer.render(this, outputStream)
}