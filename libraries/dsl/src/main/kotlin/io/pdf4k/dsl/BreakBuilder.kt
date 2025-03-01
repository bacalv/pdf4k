package io.pdf4k.dsl

import io.pdf4k.domain.Component
import java.util.Collections.unmodifiableList

class BreakBuilder<T : Component.Break>(private val type: T): ComponentBuilder<Component.Break, BreakBuilder<T>> {
    override val children: MutableList<ComponentBuilder<*, *>> = unmodifiableList(emptyList())
    override fun build() = type

    companion object {
        val blockBreakBuilder = BreakBuilder(Component.Break.BlockBreak)
        val pageBreakBuilder = BreakBuilder(Component.Break.PageBreak)
    }
}