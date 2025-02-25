package io.pdf4k.dsl

import io.pdf4k.domain.*

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
        children += ImageBuilder(ResourceLocation.Local(resource), width, height, rotation)
    }

    fun link(target: String, text: String) {
        link(target) { +text }
    }

    fun link(target: String, block: PhraseBuilder<P>.() -> Unit) {
        children += LinkBuilder(target, childBuilder().also { it.block() })
    }

    fun anchor(name: String, block: PhraseBuilder<P>.() -> Unit) {
        addChild(AnchorBuilder(name, childBuilder().also { it.block() }.children))
    }

    fun list(style: StyleAttributes? = null, block: ListBuilder<P>.() -> Unit) {
        if (style != null) {
            style(style) { list(null, block) }
        } else {
            addChild(ListBuilder(childBuilder).also { it.block() })
        }
    }

    fun crlf() = +"\n"

    class ForBlock: PhraseBuilder<ForBlock>() {
        override val childBuilder: () -> ForBlock = ::ForBlock
        private val tableBuilder: (TableAttributes, StyleAttributes?) -> TableBuilder.ForBlock =  { t, s -> TableBuilder.ForBlock(t, s) }

        fun pageNumber() {
            children += PageNumberBuilder()
        }

        fun table(columns: Int = 1, style: StyleAttributes? = null, widthPercentage: Float? = null, weights: List<Float>? = null, headerRows: Int = 0, extend: Boolean = false, block: TableBuilder.ForBlock.() -> Unit) {
            children += tableBuilder(TableAttributes(columns, widthPercentage, weights, Margin.ZERO, headerRows, extend), style).also { it.block() }
        }
    }

    class ForPage: PhraseBuilder<ForPage>() {
        override val childBuilder: () -> ForPage = ::ForPage
        private val tableBuilder: (TableAttributes, StyleAttributes?) -> TableBuilder.ForBlock = { t, s -> TableBuilder.ForBlock(t, s) }

        fun table(columns: Int = 1, style: StyleAttributes? = null, widthPercentage: Float? = null, weights: List<Float>? = null, headerRows: Int = 0, extend: Boolean = false, block: TableBuilder.ForBlock.() -> Unit) {
            children += tableBuilder(TableAttributes(columns, widthPercentage, weights, Margin.ZERO, headerRows, extend), style).also { it.block() }
        }
    }
}