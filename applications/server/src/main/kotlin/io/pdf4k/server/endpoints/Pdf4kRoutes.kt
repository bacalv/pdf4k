package io.pdf4k.server.endpoints

import io.pdf4k.server.service.Pdf4kServices

fun routes(services: Pdf4kServices) =
        RenderRoutes.routes(services)
