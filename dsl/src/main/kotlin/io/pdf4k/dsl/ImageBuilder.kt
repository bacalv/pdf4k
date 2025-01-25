package io.pdf4k.dsl

import io.pdf4k.domain.Component
import java.util.*

class ImageBuilder(
    val resource: String,
    val width: Float?,
    val height: Float?,
    val rotation: Float?
) : ComponentBuilder<Component.Image, ImageBuilder> {
    override val children: MutableList<ComponentBuilder<*, *>> = Collections.unmodifiableList(emptyList())

    override fun build() = Component.Image(resource, width, height, rotation)
}