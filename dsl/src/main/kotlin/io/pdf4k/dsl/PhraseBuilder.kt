package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.StyleAttributes

@PdfDsl
abstract class PhraseBuilder<P : PhraseBuilder<P>> : BuildsTextStyle<Component.Phrase, P> {
    override val children = mutableListOf<ComponentBuilder<*, *>>()

    override fun build() = Component.Phrase(children.map { it.build() })

    operator fun String.unaryPlus() {
        children += ChunkBuilder(this)
    }

    infix fun String.and(style: StyleAttributes) {
        children += StyleBuilder(style, mutableListOf(ChunkBuilder(this)))
    }

    fun image(resource: String, width: Float? = null, height: Float? = null, rotation: Float? = null) {
        children += ImageBuilder(resource, width, height, rotation)
    }

    fun link(target: String, text: String) {
        children += LinkBuilder(target, text)
    }

    fun anchor(name: String, block: PhraseBuilder<P>.() -> Unit) {
        addChild(AnchorBuilder(name, childBuilder().also { it.block() }.children))
    }

    fun crlf() = +"\n"

    class ForBlock: PhraseBuilder<ForBlock>() {
        override val childBuilder: () -> ForBlock = ::ForBlock

        fun pageNumber() {
            children += PageNumberBuilder()
        }
    }

    class ForPage: PhraseBuilder<ForPage>() {
        override val childBuilder: () -> ForPage = ::ForPage
    }
}