package io.pdf4k.testing

import io.pdf4k.domain.KeyName
import io.pdf4k.domain.Pdf
import io.pdf4k.domain.ResourceLocation.Companion.classpathResource
import io.pdf4k.provider.CustomResourceProvider
import io.pdf4k.provider.FontProviderFactory
import io.pdf4k.provider.KeyProvider
import io.pdf4k.provider.KeyProvider.Companion.toCertificateChain
import io.pdf4k.provider.KeyProvider.Companion.toPrivateKey
import io.pdf4k.provider.ResourceLocators
import io.pdf4k.provider.TempFileFactory.Companion.defaultTempFileFactory
import io.pdf4k.provider.TempStreamFactory.Companion.inMemoryTempStreamFactory
import io.pdf4k.provider.UriResourceLoader.Companion.defaultResourceLoader
import io.pdf4k.renderer.DocumentAssembler
import io.pdf4k.renderer.PdfRenderer
import org.junit.jupiter.api.Assertions.fail
import java.io.ByteArrayOutputStream
import java.io.OutputStream

object InMemoryRenderer {
    private val privateKey = AbstractPdfRendererTest::class.java.getResource("/certs/private-key.pem")
        ?.readText()?.let(::toPrivateKey) ?: fail("Could not load default private key")
    private val certificateChain = AbstractPdfRendererTest::class.java.getResource("/certs/cert.pem")
        ?.readText()?.let(::toCertificateChain)?.toList() ?: fail("Could not load default cert chain")

    val defaultKeyName = KeyName("default")
    val key = mutableMapOf(defaultKeyName to KeyProvider.Key(privateKey, certificateChain))
    private val keyProvider = object : KeyProvider {
        override fun lookup(keyName: KeyName) = key[keyName] ?: fail("Key not found: $keyName")
    }
    private val documentAssembler = DocumentAssembler(keyProvider)
    private val customProvider = object : CustomResourceProvider {
        override fun load(name: String) = classpathResource("/custom/$name")
    }
    private val fontProviderFactory = FontProviderFactory(defaultTempFileFactory)
    private val resourceLocators =
        ResourceLocators(defaultResourceLoader, mapOf("custom" to customProvider), fontProviderFactory)
    val renderer = PdfRenderer(resourceLocators, inMemoryTempStreamFactory, documentAssembler)

    fun Pdf.render(outputStream: OutputStream = ByteArrayOutputStream()) = renderer.render(this, outputStream)
}