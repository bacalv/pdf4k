package io.pdf4k.testing

import io.pdf4k.domain.Argument
import io.pdf4k.domain.KeyName
import io.pdf4k.domain.Pdf
import io.pdf4k.domain.ResourceLocation.Companion.classpathResource
import io.pdf4k.provider.*
import io.pdf4k.provider.KeyProvider.Companion.toCertificateChain
import io.pdf4k.provider.KeyProvider.Companion.toPrivateKey
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
    private val keyProvider = KeyProvider { keyName -> key[keyName] ?: fail("Key not found: $keyName") }
    private val documentAssembler = DocumentAssembler(keyProvider)
    private val customProvider = object : CustomResourceProvider {
        override val name: String = "custom"
        override fun load(arguments: List<Argument>, resourceLocator: ResourceLocator) = runCatching {
            val location = arguments.first { it.name == "location" } as Argument.StringArgument
            classpathResource("/custom/${location.value}")
        }.getOrNull()
    }
    private val fontProviderFactory = FontProviderFactory(defaultTempFileFactory)
    private val resourceLocators =
        DefaultResourceLocators(defaultResourceLoader, mapOf("custom" to customProvider), fontProviderFactory)
    val renderer = PdfRenderer(resourceLocators, inMemoryTempStreamFactory, documentAssembler)

    fun Pdf.render(outputStream: OutputStream = ByteArrayOutputStream()) = renderer.render(this, outputStream)
}