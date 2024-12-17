package io.pdf4k.dsl

import io.pdf4k.domain.Component
import java.util.*

class PageNumberBuilder : ComponentBuilder<Component.PageNumber, PageNumberBuilder> {
    override val children: MutableList<ComponentBuilder<*, *>> = Collections.unmodifiableList(emptyList())

    override fun build() = Component.PageNumber
}