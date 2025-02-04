package io.pdf4k.server.service

import io.pdf4k.server.service.realm.RealmService
import io.pdf4k.server.service.rendering.RenderingService

data class Pdf4kServices(
    val realmService: RealmService,
    val renderingService: RenderingService,
    val multipartFileStore: MultipartFileStore
)