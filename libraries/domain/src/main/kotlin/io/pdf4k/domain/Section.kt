package io.pdf4k.domain

data class Section(
    val stationary: List<Stationary>,
    val style: StyleAttributes?,
    val content: Component.Content,
    val blockContent: Map<String, Component.Content>,
    val backgroundImages: Map<String, ResourceLocation>
)