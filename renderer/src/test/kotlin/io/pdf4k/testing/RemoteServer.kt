package io.pdf4k.testing

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import java.net.ServerSocket

class RemoteServer {
    private lateinit var server: Http4kServer

    val handler = { request: Request ->
        val resource = this::class.java.getResourceAsStream("/remote${request.uri.path}")
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

    private fun randomPort(): Int = ServerSocket(0).run {
        close()
        localPort
    }
}