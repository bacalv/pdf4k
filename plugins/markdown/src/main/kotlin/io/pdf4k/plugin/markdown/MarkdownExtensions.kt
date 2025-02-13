package io.pdf4k.plugin.markdown

import io.pdf4k.dsl.AnyContentBuilder
import org.commonmark.parser.Parser

fun AnyContentBuilder.markdown(markdown: String) {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val renderer = Pdf4kDslMarkdownRenderer(this)
    renderer.render(document)
}