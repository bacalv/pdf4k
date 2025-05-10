package io.pdf4k.plugin.markdown

import io.pdf4k.domain.Component
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.dsl.*
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser


@JvmName("sectionMarkdown")
fun SectionContentBuilder.markdown(markdown: String, vararg imageResources: Pair<String, Component.Image>) {
    table(1, style = noBorder) {
        cell { markdown(markdown, *imageResources) }
    }
}

@JvmName("blockMarkdown")
fun BlockContentBuilder.markdown(markdown: String, vararg imageResources: Pair<String, Component.Image>) {
    table {
        cell {
            markdown(markdown, *imageResources)
        }
    }
}

fun <F : PhraseBuilder<F>, P : ParagraphBuilder<F, P>, T : TableBuilder<F, P, T, C>, C : ContentBuilder<F, P, T, C>> C.markdown(markdown: String, vararg imageResources: Pair<String, Component.Image>) {
    val extensions = listOf(TablesExtension.create())
    val parser = Parser.builder().extensions(extensions).build()
    val document = parser.parse(markdown)
    val mdRenderer = Pdf4kDslMarkdownRenderer(this, imageResources.toMap())
    mdRenderer.render(document)
}

fun localImage(resource: String, width: Float? = null, height: Float? = null, rotation: Float? = null)  =
    Component.Image(local(resource), width, height, rotation)