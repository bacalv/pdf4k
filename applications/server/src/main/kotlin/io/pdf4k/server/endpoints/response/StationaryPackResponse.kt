package io.pdf4k.server.endpoints.response

data class StationaryPackResponse(
    val name: String,
    val pageTemplates: List<String>,
    val fonts: List<String>,
    val images: List<String>
)