package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.StyleAttributes

@PdfDsl
abstract class ParagraphBuilder<F : PhraseBuilder<F>, P : ParagraphBuilder<F, P>> :
    BuildsTextStyle<Component.Paragraph, P> {
    override val children: MutableList<ComponentBuilder<*, *>> = mutableListOf()
    abstract val phraseBuilder: () -> F

    override fun build() = Component.Paragraph(children.map { it.build() })

    infix fun String.and(style: StyleAttributes) {
        phrase(this, style)
    }

    operator fun String.unaryPlus() = phrase(this)

    fun crlf() = +"\n"

    fun phrase(text: String, style: StyleAttributes? = null) {
        phrase(style) { +text }
    }

    fun phrase(block: F.() -> Unit) {
        children += phraseBuilder().also { it.block() }
    }

    fun phrase(style: StyleAttributes? = null, block: F.() -> Unit) {
        style?.let { _ -> style(style) { phrase(block) } } ?: phrase(block)
    }

    fun image(resource: String, width: Float? = null, height: Float? = null, rotation: Float? = null) {
        phrase { image(resource, width, height, rotation) }
    }

    fun link(target: String, text: String, style: StyleAttributes? = null) {
        phrase(style) { link(target, text) }
    }

    fun link(target: String, style: StyleAttributes? = null, block: PhraseBuilder<F>.() -> Unit) {
        phrase(style) { link(target, block) }
    }

    fun anchor(name: String, text: String) {
        phrase { anchor(name) { +text } }
    }

    class ForBlock : ParagraphBuilder<PhraseBuilder.ForBlock, ForBlock>() {
        override val childBuilder: () -> ForBlock = ::ForBlock
        override val phraseBuilder: () -> PhraseBuilder.ForBlock = { PhraseBuilder.ForBlock() }

        fun pageNumber() {
            phrase { pageNumber() }
        }
    }

    class ForPage : ParagraphBuilder<PhraseBuilder.ForPage, ForPage>() {
        override val childBuilder: () -> ForPage = ::ForPage
        override val phraseBuilder: () -> PhraseBuilder.ForPage = { PhraseBuilder.ForPage() }
    }
}