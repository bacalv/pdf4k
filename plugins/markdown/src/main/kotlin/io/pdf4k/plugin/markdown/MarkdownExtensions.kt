package io.pdf4k.plugin.markdown

import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.dsl.AnyContentBuilder
import io.pdf4k.dsl.AnyTableBuilder
import org.commonmark.parser.Parser

fun AnyContentBuilder.markdown(markdown: String) {
    table(1, style = noBorder) {
        markdown(markdown)
    }
}

fun AnyTableBuilder.markdown(markdown: String) {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val mdRenderer = Pdf4kDslMarkdownRenderer(this)
    mdRenderer.render(document)
}