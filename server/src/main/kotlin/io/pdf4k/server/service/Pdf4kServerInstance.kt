package io.pdf4k.server.service

import org.http4k.contract.ContractRoute
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.core.HttpHandler
import org.http4k.format.Jackson
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import java.util.concurrent.atomic.AtomicReference

class Pdf4kServerInstance(
    private val routes: List<ContractRoute>,
    private val factory: (HttpHandler) -> Http4kServer
) {
    private val server = AtomicReference<Http4kServer>()
    val port get() = server.get().port()

    fun start() {
        val handler = contract {
            renderer = OpenApi3(ApiInfo("PDF4k Server", "v0.0.1", "PDF4k Server API"), Jackson)
            descriptionPath = "/openapi.json"
            routes += this@Pdf4kServerInstance.routes
        }
        val routes = routes(
            handler,
            swaggerUiLite {
                url = "/openapi.json"
                pageTitle = "PDF4k Server API"
                persistAuthorization = true
            }
        )
        val instance = factory(routes)
        server.set(instance)
        instance.start()
    }

    fun stop() {
        server.get()?.stop()
        server.set(null)
    }
}