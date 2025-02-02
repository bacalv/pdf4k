package io.pdf4k.domain.dto

import io.pdf4k.domain.Page

data class PageDto(
    val stationary: List<StationaryRef>,
    val style: StyleRef?,
    val content: ComponentDto.Content,
    val blockContent: Map<String, ComponentDto.Content>
)

fun Page.toDto(resourceMapBuilder: ResourceMap.Builder) = PageDto(
    stationary = stationary.map { it.toDto(resourceMapBuilder).let(resourceMapBuilder::stationaryRef) },
    style = style?.toDto(resourceMapBuilder)?.let(resourceMapBuilder::styleRef),
    content = content.toDto(resourceMapBuilder) as ComponentDto.Content,
    blockContent = blockContent.map { it.key to it.value.toDto(resourceMapBuilder) as ComponentDto.Content }.toMap()
)