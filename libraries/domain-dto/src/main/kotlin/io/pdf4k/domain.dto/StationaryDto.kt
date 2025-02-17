package io.pdf4k.domain.dto

import io.pdf4k.domain.Stationary

data class StationaryDto(
    val template: ResourceRef,
    val templatePage: Int = 1,
    val width: Float,
    val height: Float,
    val blocks: Map<String, BlockDto> = emptyMap(),
    val contentFlow: List<String> = emptyList()
)

fun Stationary.toDto(resourceMapBuilder: ResourceMapDto.Builder) = StationaryDto(
    template = template.toDto(resourceMapBuilder).let(resourceMapBuilder::resourceRef),
    templatePage = templatePage,
    width = width,
    height = height,
    blocks = blocks.map { it.key to it.value.toDto() }.toMap(),
    contentFlow = contentFlow
)

fun StationaryDto.toDomain(resourceMap: ResourceMap) = Stationary(
    template = resourceMap.getResourceLocation(template),
    templatePage = templatePage,
    width = width,
    height = height,
    blocks = blocks.map { it.key to it.value.toDomain() }.toMap(),
    contentFlow = contentFlow
)