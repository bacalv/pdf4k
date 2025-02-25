package io.pdf4k.plugin.markdown

import io.pdf4k.domain.StyleAttributes.Companion.border
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.domain.StyleAttributes.Companion.padding
import io.pdf4k.dsl.BlockContentBuilder
import io.pdf4k.dsl.PageContentBuilder
import io.pdf4k.dsl.PhraseBuilder
import io.pdf4k.dsl.TableBuilder
import org.commonmark.parser.Parser
import java.awt.Color.BLACK

@JvmName("pageMarkdown")
fun PageContentBuilder.markdown(markdown: String) {
    table(1, style = noBorder) { markdown(markdown) }
}

@JvmName("blockMarkdown")
fun BlockContentBuilder.markdown(markdown: String) {
    table(1, style = noBorder) { markdown(markdown) }
}

fun <F : PhraseBuilder<F>, T : TableBuilder<F, T>> T.markdown(markdown: String) {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    tableCell(1, padding(0f) + border(1f, BLACK)) {
        val mdRenderer = Pdf4kDslMarkdownRenderer(this)
        mdRenderer.render(document)
    }
}