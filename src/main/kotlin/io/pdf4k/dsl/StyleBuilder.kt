package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.StyleAttributes

class StyleBuilder(
    val attributes: StyleAttributes,
    override val children: MutableList<ComponentBuilder<*, *>>
) : ComponentBuilder<Component.Style, StyleBuilder> {
    override fun build() = Component.Style(attributes, children.map { it.build() })
}