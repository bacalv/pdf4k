package io.pdf4k.dsl

import io.pdf4k.domain.*

@PdfDsl
class SectionBuilder(val style: StyleAttributes?, private val stationary: List<Stationary>) {
    private val builder = ContentBuilder.ForSection()
    private val blockContent = mutableMapOf<String, Component.Content>()
    private val backgroundImages = mutableMapOf<String, ResourceLocation>()

    fun content(style: StyleAttributes? = null, block: ContentBuilder.ForSection.() -> Unit) {
        if (style == null) {
            builder.block()
        } else {
            builder.style(style) {
                block()
            }
        }
    }

    fun block(ref: String, style: StyleAttributes? = null, backgroundImage: ResourceLocation? = null, block: ContentBuilder.ForBlock.() -> Unit) {
        val builder = ContentBuilder.ForBlock()
        if (style == null) {
            builder.block()
        } else {
            builder.style(style) {
                block()
            }
        }
        blockContent[ref] = builder.build()
        backgroundImage?.let { backgroundImages[ref] = it }
    }

    fun build() = Section(stationary, style, builder.build(), blockContent, backgroundImages)
}