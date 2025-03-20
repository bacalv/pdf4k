package io.pdf4k.server.endpoints

import io.pdf4k.server.config.Pdf4kServerConfiguration
import io.pdf4k.server.service.Pdf4kServices
import kotlin.reflect.full.primaryConstructor

fun Pdf4kServerConfiguration.routes(services: Pdf4kServices) =
        RenderRoutes.routes(services, endpoints.mapNotNull {
                runCatching {
                        Class.forName(it).kotlin.primaryConstructor?.call() as? PdfEndpoint<*, *>
                }.getOrNull()
        })
