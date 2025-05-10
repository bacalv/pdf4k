package io.pdf4k.domain.dto

import io.pdf4k.domain.Component
import io.pdf4k.domain.Section

data class SectionDto(
    val stationary: List<StationaryRef>,
    val style: StyleRef?,
    val content: List<ComponentDto>,
    val blockContent: Map<String, List<ComponentDto>>,
    val backgroundImages: Map<String, ResourceRef>
)

fun Section.toDto(resourceMapBuilder: ResourceMapDto.Builder) = SectionDto(
    stationary = stationary.map { it.toDto(resourceMapBuilder).let(resourceMapBuilder::stationaryRef) },
    style = style?.toDto(resourceMapBuilder)?.let(resourceMapBuilder::styleRef),
    content = (content.toDto(resourceMapBuilder) as ComponentDto.Content).children,
    blockContent = blockContent.map { it.key to it.value.toDto(resourceMapBuilder) as ComponentDto.Content }
        .associate { (key, value) -> key to value.children },
    backgroundImages = backgroundImages.map { it.key to resourceMapBuilder.resourceRef(it.value.toDto(resourceMapBuilder))}.toMap()
)

fun SectionDto.toDomain(resourceMap: ResourceMap): Section = Section(
    stationary = stationary.map { resourceMap.getStationary(it) },
    style = style?.let { resourceMap.getStyle(it) },
    content = Component.Content(content.toDomain(resourceMap)),
    blockContent = blockContent.map {
        it.key to Component.Content(it.value.toDomain(resourceMap))
    }.toMap(),
    backgroundImages = backgroundImages.map {
        it.key to resourceMap.getResourceLocation(it.value)
    }.toMap()
)