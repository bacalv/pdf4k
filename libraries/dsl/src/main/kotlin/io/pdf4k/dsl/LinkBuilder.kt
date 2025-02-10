package io.pdf4k.dsl

import io.pdf4k.domain.Component.Link
import java.util.*

class LinkBuilder(private val target: String, private val text: String): ComponentBuilder<Link, LinkBuilder> {
    override val children: MutableList<ComponentBuilder<*, *>> = Collections.unmodifiableList(emptyList())

    override fun build() = Link(target, text)
}