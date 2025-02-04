package io.pdf4k.server.domain

data class StationaryPack(
    val name: String,
    val pageTemplates: MutableMap<String, FileId> = mutableMapOf(),
    val fonts: MutableMap<String, FileId> = mutableMapOf(),
    val images: MutableMap<String, FileId> = mutableMapOf()
)