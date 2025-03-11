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
                
                Blah blah blah
                blah blah blah
                
                blah blah blah
              
                This is *italic*.
                This is **bold**.
                This is ***bold italic***.
                
                This is another paragraph
                
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
                2. Second item
                    1. Nested list item 1
                    2. Nested list item 2
                3. Third item
                    - Dashed nested list item 1
                    - Dashed nested list item 2
                    
                > Block quote with a list in...
                >
                > 1. Do this
                > 2. Then this
                >    - Not forgetting this
                > 3. Finally this
                >
                > End
                
                List with a blockquote in...
                
                1. Something
                    > Block quote
                    > More blockquote
                    > > Nested blockquote with list in
                    > > 1. One
                    > > 2. Two
                    > > 3. Three
                2. Something else
                
                # Tables
                
                | Name    | Rank       | Notes
                | ------- | ---------- | -----
                | Fred    | First      | Winner
                | Jim     | Second     | Runner up
                | Dave    | Third      | Last
                
                # Code block
                
                Code block:
                
                    10 INPUT "WHAT IS YOUR NAME?", A$
                    20 PRINT "HELLO, " + A$
                    30 GOTO 20
                    
                Inline `code` literal
                
                # Images
                
                ![Some Image](image-1)
            """.trimIndent(),
                "image-1" to localImage("img.png", 160f, 120f)
            )
        }.approve(approver)
    }
}