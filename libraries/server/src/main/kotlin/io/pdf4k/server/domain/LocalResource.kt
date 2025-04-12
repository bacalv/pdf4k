package io.pdf4k.server.domain

import java.io.File

sealed interface LocalResource {
    data object Classpath: LocalResource
    data class DownloadedFile(val file: File): LocalResource
}