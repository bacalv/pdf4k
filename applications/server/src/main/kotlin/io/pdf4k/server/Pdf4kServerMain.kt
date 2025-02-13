package io.pdf4k.server

import io.pdf4k.domain.PdfError
import io.pdf4k.provider.KeyProvider
import io.pdf4k.provider.KeyProvider.Companion.toCertificateChain
import io.pdf4k.provider.KeyProvider.Companion.toPrivateKey
import io.pdf4k.provider.TempStreamFactory.Companion.inMemoryTempStreamFactory
import io.pdf4k.renderer.DocumentAssembler
import io.pdf4k.server.config.Pdf4kServerConfiguration
import io.pdf4k.server.config.Pdf4kServerConfiguration.Companion.loadConfig
import io.pdf4k.server.endpoints.routes
import io.pdf4k.server.service.MultipartFileStore.Companion.tempFileMultipartFileStore
import io.pdf4k.server.service.Pdf4kServerInstance
import io.pdf4k.server.service.Pdf4kServices
import io.pdf4k.server.service.realm.RealmService
import io.pdf4k.server.service.rendering.RenderingService
import org.http4k.server.Undertow
import org.http4k.server.asServer

object Pdf4kServerMain {
    @JvmStatic
    fun main(args: Array<String>) {
        loadConfig().pdf4kServer().start()
    }
}

fun Pdf4kServerConfiguration.pdf4kServer(): Pdf4kServerInstance {
    val realmService = RealmService()
    val documentAssembler = DocumentAssembler { keyName ->
        runCatching {
            val cert = Pdf4kServerConfiguration::class.java.getResourceAsStream("/keys/${keyName.name}/cert.pem")!!
            val pk = Pdf4kServerConfiguration::class.java.getResourceAsStream("/keys/${keyName.name}/private-key.pem")!!
            KeyProvider.Key(toPrivateKey(String(pk.readAllBytes())), toCertificateChain(String(cert.readAllBytes())))
        }.getOrElse { throw PdfError.KeyNotFound(keyName.name) }
    }
    val renderingService = RenderingService(realmService, inMemoryTempStreamFactory, documentAssembler, tempFileMultipartFileStore)
    val services = Pdf4kServices(realmService, renderingService, tempFileMultipartFileStore)
    return Pdf4kServerInstance(routes(services)) { it.asServer(Undertow(port)) }
}