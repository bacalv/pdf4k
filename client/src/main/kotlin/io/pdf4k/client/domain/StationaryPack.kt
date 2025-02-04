package io.pdf4k.client.domain

data class StationaryPack(
    val name: String,
    val pageTemplates: List<String>,
    val fonts: List<String>,
    val images: List<String>
)