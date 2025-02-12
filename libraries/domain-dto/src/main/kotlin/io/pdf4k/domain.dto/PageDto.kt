package io.pdf4k.domain.dto

import io.pdf4k.domain.Component
import io.pdf4k.domain.Page

data class PageDto(
    val stationary: List<StationaryRef>,
    val style: StyleRef?,
    val content: List<ComponentDto>,
    val blockContent: Map<String, List<ComponentDto>>
)

fun Page.toDto(resourceMapBuilder: ResourceMapDto.Builder) = PageDto(
    stationary = stationary.map { it.toDto(resourceMapBuilder).let(resourceMapBuilder::stationaryRef) },
    style = style?.toDto(resourceMapBuilder)?.let(resourceMapBuilder::styleRef),
    content = (content.toDto(resourceMapBuilder) as ComponentDto.Content).children,
    blockContent = blockContent.map { it.key to it.value.toDto(resourceMapBuilder) as ComponentDto.Content }
        .associate { (key, value) -> key to value.children }
)

fun PageDto.toDomain(resourceMap: ResourceMap): Page = Page(
    stationary = stationary.map { resourceMap.getStationary(it) },
    style = style?.let { resourceMap.getStyle(it) },
    content = Component.Content(content.toDomain(resourceMap)),
    blockContent = blockContent.map {
        it.key to Component.Content(it.value.toDomain(resourceMap))
    }.toMap()
)