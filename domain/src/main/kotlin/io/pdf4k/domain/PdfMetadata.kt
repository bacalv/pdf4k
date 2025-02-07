package io.pdf4k.domain

data class PdfMetadata(
    val title: String?,
    val author: String?,
    val subject: String?,
    val keywords: String?,
    val creator: String?,
    val producer: String?,
    val customProperties: Map<String, String> = emptyMap()
) {
    companion object {
        val empty = PdfMetadata(null, null, null, null, null, null, emptyMap())
    }
}
