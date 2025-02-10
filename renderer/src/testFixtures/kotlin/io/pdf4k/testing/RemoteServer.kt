package io.pdf4k.testing

import io.pdf4k.domain.ResourceLocation.Companion.classpathResource
import io.pdf4k.testing.domain.randomPort
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer

class RemoteServer {
    private lateinit var server: Http4kServer

    val handler = { request: Request ->
        val resource = classpathResource("/remote${request.uri.path}")
        resource?.let {
            Response(OK).body(resource)
        } ?: Response(NOT_FOUND)
    }

    fun start(): Int {
        val port = randomPort()
        server = handler.asServer(Undertow(port)).start()
        return port
    }

    fun stop() {
        server.stop()
    }
}