package io.pdf4k.renderer

import com.lowagie.text.Chunk
import io.pdf4k.domain.Page
import io.pdf4k.renderer.ComponentRenderer.render

object PageRenderer {
    fun Page.render(context: RendererContext) {
        context.pushStyle(style)
        content.children.ifEmpty { context.add(listOf(Chunk(""))) }
        context.add(listOf(content).render(context))
        context.popStyle()
    }
}