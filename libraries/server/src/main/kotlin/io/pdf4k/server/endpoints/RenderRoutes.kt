package io.pdf4k.server.endpoints

import io.pdf4k.server.service.Pdf4kServices
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import java.util.*

object RenderRoutes {
    fun routes(services: Pdf4kServices, endpoints: List<PdfEndpoint<*>>) = endpoints.map { endpoint ->
        endpoint.path meta {
            summary = endpoint.summary
            endpoint.receiving(this)
            returning(OK)
        } bindContract POST to { request ->
            val pdf = endpoint.pdfFor(request)
            runCatching {
                services.renderingService.render(pdf)
            }.map { inputStream ->
                Response(OK)
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"${UUID.randomUUID()}.pdf\"")
                    .body(inputStream)
            }.getOrElse(ErrorHandler::invoke)
        }
    }
}