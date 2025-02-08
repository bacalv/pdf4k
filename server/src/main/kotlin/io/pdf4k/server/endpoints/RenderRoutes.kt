package io.pdf4k.server.endpoints

import io.pdf4k.domain.dto.toDomain
import io.pdf4k.server.domain.ServerLens.pdfRequestLens
import io.pdf4k.server.domain.ServerLens.realmPathLens
import io.pdf4k.server.domain.ServerLens.stationaryPackPathLens
import io.pdf4k.server.service.Pdf4kServices
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK

object RenderRoutes {
    fun routes(services: Pdf4kServices) = listOf(
        "/realms" / realmPathLens / stationaryPackPathLens / "render" meta {
            summary = "Renders a PDF."
            receiving(pdfRequestLens)
            returning(OK)
        } bindContract POST to { realmName, stationaryPackName, _ ->
            { request ->
                val (pdf, resourceMap) = pdfRequestLens(request).pdf.toDomain()
                val inputStream = services.renderingService.render(realmName, stationaryPackName, pdf, resourceMap)
                Response(OK)
                    .header("Content-Type", "application/pdf")
                    .body(inputStream, null)
            }
        }
    )
}