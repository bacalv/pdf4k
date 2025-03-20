package io.pdf4k.server.service

import io.pdf4k.server.service.rendering.AsyncRenderingService
import io.pdf4k.server.service.rendering.RenderingService

data class Pdf4kServices(
    val renderingService: RenderingService,
    val asyncRenderingService: AsyncRenderingService
)