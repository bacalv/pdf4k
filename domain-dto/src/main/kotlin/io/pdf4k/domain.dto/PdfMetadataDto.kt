package io.pdf4k.domain.dto

import io.pdf4k.domain.PdfMetadata

data class PdfMetadataDto(
    val title: String?,
    val author: String?,
    val subject: String?,
    val keywords: String?,
    val creator: String?,
    val producer: String?,
    val customProperties: Map<String, String> = emptyMap()
)

fun PdfMetadata.toDto() = PdfMetadataDto(title, author, subject, keywords, creator, producer, customProperties)
fun PdfMetadataDto.toDomain() = PdfMetadata(title, author, subject, keywords, creator, producer, customProperties)