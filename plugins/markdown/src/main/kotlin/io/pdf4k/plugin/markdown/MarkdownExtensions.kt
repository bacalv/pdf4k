package io.pdf4k.plugin.markdown

import io.pdf4k.domain.Component
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.domain.StyleAttributes.Companion.padding
import io.pdf4k.dsl.BlockContentBuilder
import io.pdf4k.dsl.PageContentBuilder
import io.pdf4k.dsl.PhraseBuilder
import io.pdf4k.dsl.TableBuilder
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser


@JvmName("pageMarkdown")
fun PageContentBuilder.markdown(markdown: String, vararg imageResources: Pair<String, Component.Image>) {
    table(1, style = noBorder) { markdown(markdown, *imageResources) }
}

@JvmName("blockMarkdown")
fun BlockContentBuilder.markdown(markdown: String, vararg imageResources: Pair<String, Component.Image>) {
    markdown(markdown, *imageResources)
}

fun <F : PhraseBuilder<F>, T : TableBuilder<F, T>> T.markdown(markdown: String, vararg imageResources: Pair<String, Component.Image>) {
    val extensions = listOf(TablesExtension.create())
    val parser = Parser.builder().extensions(extensions).build()
    val document = parser.parse(markdown)
    tableCell(1, padding(0f)) {
        val mdRenderer = Pdf4kDslMarkdownRenderer(this, imageResources.toMap())
        mdRenderer.render(document)
    }
}

fun localImage(resource: String, width: Float? = null, height: Float? = null, rotation: Float? = null)  =
    Component.Image(local(resource), width, height, rotation)