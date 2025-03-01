package io.pdf4k.dsl

import io.pdf4k.domain.Component
import io.pdf4k.domain.Margin
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.TableAttributes
import io.pdf4k.dsl.BreakBuilder.Companion.blockBreakBuilder
import io.pdf4k.dsl.BreakBuilder.Companion.pageBreakBuilder

@PdfDsl
abstract class ContentBuilder<F : PhraseBuilder<F>, P : ParagraphBuilder<F, P>, T : TableBuilder<F, T>, C : ContentBuilder<F, P, T, C>> :
    BuildsCellStyle<Component.Content, C> {
    override val children = mutableListOf<ComponentBuilder<*, *>>()
    abstract val tableBuilder: (TableAttributes, StyleAttributes?) -> T
    abstract val phraseBuilder: () -> F
    abstract val paragraphBuilder: () -> P

    operator fun String.unaryPlus() = paragraph(this)

    fun crlf() {
        paragraph("\n")
    }

    fun paragraph(text: String) {
        val builder = paragraphBuilder()
        builder.phrase(text)
        children += builder
    }

    fun paragraph(style: StyleAttributes? = null, block: P.() -> Unit) {
        if (style != null) {
            style(style) {
                paragraph(null, block)
            }
        } else {
            children += paragraphBuilder().also { it.block() }
        }
    }

    fun phrase(text: String) {
        phrase { +text }
    }

    fun phrase(block: F.() -> Unit) {
        children += phraseBuilder().also { it.block() }
    }

    fun phrase(style: StyleAttributes, block: F.() -> Unit) {
        style(style) { phrase(block) }
    }

    fun table(columns: Int = 1, style: StyleAttributes? = null, widthPercentage: Float? = null, weights: List<Float>? = null, headerRows: Int = 0, extend: Boolean = false, block: T.() -> Unit) {
        children += tableBuilder(TableAttributes(columns, widthPercentage, weights, Margin.ZERO, headerRows, extend), style).also { it.block() }
    }

    fun list(style: StyleAttributes? = null, block: ListBuilder<F, T>.() -> Unit) {
        table(1, style) { listCell { this.block() } }
    }

    override fun build() = Component.Content(children.map { it.build() })

    class ForBlock : ContentBuilder<PhraseBuilder.ForBlock, ParagraphBuilder.ForBlock, TableBuilder.ForBlock, ForBlock>() {
        override val phraseBuilder: () -> PhraseBuilder.ForBlock = { PhraseBuilder.ForBlock() }
        override val paragraphBuilder: () -> ParagraphBuilder.ForBlock = { ParagraphBuilder.ForBlock() }
        override val childBuilder: () -> ForBlock = ::ForBlock
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> TableBuilder.ForBlock =  { t, s -> TableBuilder.ForBlock(t, s) }
    }

    class ForPage : ContentBuilder<PhraseBuilder.ForPage, ParagraphBuilder.ForPage, TableBuilder.ForPage, ForPage>() {
        override val phraseBuilder: () -> PhraseBuilder.ForPage = { PhraseBuilder.ForPage() }
        override val paragraphBuilder: () -> ParagraphBuilder.ForPage = { ParagraphBuilder.ForPage() }
        override val childBuilder: () -> ForPage = ::ForPage
        override val tableBuilder: (TableAttributes, StyleAttributes?) -> TableBuilder.ForPage =  { t, s -> TableBuilder.ForPage(t, s) }

        fun blockBreak() { children += blockBreakBuilder }

        fun pageBreak() { children += pageBreakBuilder }
    }
}

typealias AnyContentBuilder = ContentBuilder<*, *, *, *>
typealias PageContentBuilder = ContentBuilder<PhraseBuilder.ForPage, ParagraphBuilder.ForPage, TableBuilder.ForPage, ContentBuilder.ForPage>
typealias BlockContentBuilder = ContentBuilder<PhraseBuilder.ForBlock, ParagraphBuilder.ForBlock, TableBuilder.ForBlock, ContentBuilder.ForBlock>
