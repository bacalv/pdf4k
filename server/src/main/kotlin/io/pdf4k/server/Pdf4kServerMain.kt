package io.pdf4k.server

import io.pdf4k.domain.KeyName
import io.pdf4k.provider.KeyProvider
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

fun main() {
    loadConfig().pdf4kServer().start()
}

fun Pdf4kServerConfiguration.pdf4kServer(): Pdf4kServerInstance {
    val realmService = RealmService()
    val documentAssembler = DocumentAssembler(object : KeyProvider {
        override fun lookup(keyName: KeyName): KeyProvider.Key {
            TODO("Not yet implemented")
        }
    })
    val renderingService = RenderingService(realmService, inMemoryTempStreamFactory, documentAssembler, tempFileMultipartFileStore)
    val services = Pdf4kServices(realmService, renderingService, tempFileMultipartFileStore)
    return Pdf4kServerInstance(routes(services)) { it.asServer(Undertow(port)) }
}