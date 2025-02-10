package io.pdf4k.plugin.markdown

import io.pdf4k.dsl.ContentBuilder
import org.commonmark.parser.Parser

fun ContentBuilder<*, *, *, *>.markdown(markdown: String) {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val renderer = PdfMarkdownRenderer(this)
    renderer.render(document)
}