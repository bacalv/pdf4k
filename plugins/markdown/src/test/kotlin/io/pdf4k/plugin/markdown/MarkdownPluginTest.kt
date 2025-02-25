package io.pdf4k.plugin.markdown

import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.testing.AbstractPdfRendererTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test

class MarkdownPluginTest: AbstractPdfRendererTest() {
    @Test
    fun `renders markdown`(approver: PdfApprover) {
        content {
            markdown("""
                # Heading 1
                
                ## Heading 2
                
                ### Heading 3
                
                #### Heading 4
                
                ##### Heading 5
                
                ###### Heading 6

                # Bold / Italic
                
                This is *italic*.
                This is **bold**.
                This is ***bold italic***.
                
                # Block quotes
                
                > ### Heading in a block quote
                > Some more text
                >> Nested text
                >
                > After nested
                
                # Links
                
                [Look **here** that was *bold*](https://github.com/commonmark/commonmark.js)
                
                > [Link](https://pdf4k.io) in a **block quote**!
                
                # Lists
                
                1. First item
                1. Second item
                1. Third item
            """.trimIndent())
        }.approve(approver)
    }
}