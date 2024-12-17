package io.pdf4k.dsl

import io.pdf4k.domain.Component.Anchor

class AnchorBuilder(val name: String, override val children: MutableList<ComponentBuilder<*, *>>) : ComponentBuilder<Anchor, AnchorBuilder> {
    override fun build() = Anchor(name, children.map { it.build() })
}