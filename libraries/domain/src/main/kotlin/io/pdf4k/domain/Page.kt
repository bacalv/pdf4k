package io.pdf4k.domain

data class Page(
    val stationary: List<Stationary>,
    val style: StyleAttributes?,
    val content: Component.Content,
    val blockContent: Map<String, Component.Content>
)