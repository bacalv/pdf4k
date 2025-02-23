package io.pdf4k.approval

import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.testing.AbstractPdfRendererTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import java.awt.Color

class ListsTest : AbstractPdfRendererTest() {
    @Test
    fun `renders a nested unordered list`(approver: PdfApprover) {
        content {
            phrase {
                list {
                    item("This is the first item")
                    item {
                        +"This is the "
                        "second" and style(fontStyle = Bold)
                    }.list {
                        style(colour = Color.RED) {
                            item("This is a nested item under the second item")
                            item("This is another nested item under the second item")
                        }
                    }
                    item("This is the third item")
                }
            }
        }.approve(approver)
    }
}