package io.pdf4k.dsl

import io.pdf4k.domain.Component

interface ComponentBuilder<T : Component, B : ComponentBuilder<T, B>> {
    val children: MutableList<ComponentBuilder<*, *>>

    fun addChild(builder: ComponentBuilder<*, *>) = children.add(builder)

    fun build(): T
}