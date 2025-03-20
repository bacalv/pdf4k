package io.pdf4k.server.endpoints

import io.pdf4k.server.domain.AsyncPdfResponse
import io.pdf4k.server.domain.ServerLens.asyncPdfResponseLens
import io.pdf4k.server.service.Pdf4kServices
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import java.util.*

object RenderRoutes {
    fun routes(services: Pdf4kServices, endpoints: List<PdfEndpoint<*, *>>) = endpoints.map { endpoint ->
        endpoint.path meta {
            summary = endpoint.summary
            endpoint.receiving(this)
            returning(if (endpoint is AsyncPdfEndpoint<*, *>) ACCEPTED else OK)
        } bindContract POST to { request ->
            when (endpoint) {
                is AsyncPdfEndpoint<*, *> -> {
                    val asyncRequest = endpoint.requestLens(request)
                    val pdfFn = { endpoint.pdfFor(request) }
                    val jobId = services.asyncRenderingService.submitJob(asyncRequest, pdfFn)
                    Response(ACCEPTED).with(asyncPdfResponseLens of AsyncPdfResponse(jobId))
                }
                is SyncPdfEndpoint<*> -> {
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
                else -> throw IllegalStateException("Unknown endpoint type")
            }
        }
    }
}