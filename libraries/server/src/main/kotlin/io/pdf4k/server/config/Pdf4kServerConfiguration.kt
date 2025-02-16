package io.pdf4k.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.pdf4k.domain.PdfError
import io.pdf4k.provider.KeyProvider
import io.pdf4k.provider.KeyProvider.Companion.toCertificateChain
import io.pdf4k.provider.KeyProvider.Companion.toPrivateKey
import io.pdf4k.provider.TempStreamFactory.Companion.inMemoryTempStreamFactory
import io.pdf4k.renderer.DocumentAssembler
import io.pdf4k.server.endpoints.DefaultEndpoint
import io.pdf4k.server.endpoints.routes
import io.pdf4k.server.service.MultipartFileStore.Companion.tempFileMultipartFileStore
import io.pdf4k.server.service.Pdf4kServerInstance
import io.pdf4k.server.service.Pdf4kServices
import io.pdf4k.server.service.rendering.RenderingService
import org.http4k.server.Undertow
import org.http4k.server.asServer
import java.io.FileInputStream

data class Pdf4kServerConfiguration(
    val port: Int,
    val endpoints: List<String> = DefaultEndpoint::class.qualifiedName?.let { listOf(it) }
        ?: throw IllegalStateException("Impossible")
) {
    companion object {
        const val CONFIG_PROPERTY_KEY = "PDF4K_CONFIG_LOCATION"
        const val DEFAULT_CONFIG = "/default-pdf4k-config.yaml"

        fun loadConfig(from: String? = null): Pdf4kServerConfiguration {
            val configLocation = from
                ?: System.getProperty(CONFIG_PROPERTY_KEY)
                ?: System.getenv(CONFIG_PROPERTY_KEY)
                ?: DEFAULT_CONFIG

            val bytes = Pdf4kServerConfiguration::class.java.getResource(configLocation)?.openStream()?.readAllBytes()
                ?: runCatching {
                    FileInputStream(configLocation).readAllBytes()
                }.getOrNull() ?: throw PdfError.CannotLoadConfiguration(configLocation)
            val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule.Builder().build())
            return mapper.readValue<Pdf4kServerConfiguration>(bytes)
        }
    }

    fun pdf4kServer(): Pdf4kServerInstance {
        val documentAssembler = DocumentAssembler { keyName ->
            runCatching {
                val cert = Pdf4kServerConfiguration::class.java.getResourceAsStream("/keys/${keyName.name}/cert.pem")!!
                val pk = Pdf4kServerConfiguration::class.java.getResourceAsStream("/keys/${keyName.name}/private-key.pem")!!
                KeyProvider.Key(toPrivateKey(String(pk.readAllBytes())), toCertificateChain(String(cert.readAllBytes())))
            }.getOrElse { throw PdfError.KeyNotFound(keyName.name) }
        }
        val renderingService = RenderingService(inMemoryTempStreamFactory, documentAssembler, tempFileMultipartFileStore)
        val services = Pdf4kServices(renderingService, tempFileMultipartFileStore)
        return Pdf4kServerInstance(routes(services)) { it.asServer(Undertow(port)) }
    }
}