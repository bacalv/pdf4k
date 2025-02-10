package io.pdf4k.dsl

import io.pdf4k.domain.Component

class ChunkBuilder(private val text: String): ComponentBuilder<Component.Chunk, ChunkBuilder> {
    override val children: MutableList<ComponentBuilder<*, *>> = mutableListOf()

    override fun build() =  Component.Chunk(text)
}