package io.pdf4k.server.scenario

import io.pdf4k.testing.domain.randomPort
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Path
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CallbackServer {
    private lateinit var server: Http4kServer

    private val receivedPdfs = ConcurrentHashMap<UUID, ByteArray>()

    private val handler = routes(
        "/pdf-callback/$jobRefLens" bind POST to { request ->
            val ref = jobRefLens(request)
            val bytes = ByteArrayOutputStream()
            request.body.stream.copyTo(bytes)
            receivedPdfs[ref] = bytes.toByteArray()
            Response(OK)
        }
    )

    fun start(): Int {
        val port = randomPort()
        server = handler.asServer(Undertow(port)).start()
        return port
    }

    fun stop() {
        server.stop()
    }

    operator fun get(jobRef: UUID) = receivedPdfs[jobRef]

    companion object {
        val jobRefLens = Path.map({ UUID.fromString(it) }, { it.toString() }).of("ref")
    }
}