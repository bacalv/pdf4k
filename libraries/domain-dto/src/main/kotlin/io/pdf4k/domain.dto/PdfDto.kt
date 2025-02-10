package io.pdf4k.domain.dto

import io.pdf4k.domain.Pdf

data class PdfDto(
    val resourceMap: ResourceMapDto,
    val style: StyleRef?,
    val pages: List<PageDto>,
    val metadata: PdfMetadataDto,
    val signature: SignatureDto?,
    val permissions: PdfPermissionsDto?
)

fun Pdf.toDto(): PdfDto {
    val resourceMapBuilder = ResourceMapDto.Builder()
    val style = style?.toDto(resourceMapBuilder)?.let(resourceMapBuilder::styleRef)
    val pages = pages.map { it.toDto(resourceMapBuilder) }
    val metadata = metadata.toDto()
    val signature = signature?.toDto()
    val permissions = permissions?.toDto()
    return PdfDto(resourceMapBuilder.build(), style, pages, metadata, signature, permissions)
}

fun PdfDto.toDomain(): Pair<Pdf, ResourceMap> {
    val resourceMap = ResourceMap(resourceMap)
    val pdf = Pdf(
        style = style?.let{ resourceMap.getStyle(it) },
        pages = pages.map { it.toDomain(resourceMap) },
        metadata = metadata.toDomain(),
        signature = signature?.toDomain(),
        permissions = permissions?.toDomain()
    )
    return pdf to resourceMap
}