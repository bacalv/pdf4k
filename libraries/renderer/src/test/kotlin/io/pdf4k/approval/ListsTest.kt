package io.pdf4k.approval

import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.domain.VerticalAlignment
import io.pdf4k.dsl.ListBuilder.Companion.dashed
import io.pdf4k.dsl.ListBuilder.Companion.numbered
import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.testing.AbstractPdfRendererTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import java.awt.Color

class ListsTest : AbstractPdfRendererTest() {
    @Test
    fun `renders a nested unordered list`(approver: PdfApprover) {
        content {
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
        }.approve(approver)
    }

    @Test
    fun `renders a nested ordered list`(approver: PdfApprover) {
        content {
            style(colour = Color.BLUE) {
                list(numbered) {
                    item("This is the first item")
                    item {
                        +"This is the "
                        "second" and style(fontStyle = Bold)
                    }.list(dashed + style(colour = Color.ORANGE)) {
                        style(colour = Color.RED) {
                            item("This is a nested item under the second item")
                            item("This is another nested item under the second item")
                        }
                    }
                    item("This is the third item")
                    style(colour = Color.ORANGE, size = 24f, valign = VerticalAlignment.Middle) {
                        item {
                            style(colour = Color.BLUE, size = 12f) {
                                +"This is the fourth"
                            }
                        }
                    }
                }

                list(dashed + style(colour = Color.GREEN)) {
                    item("This is another list")
                    item("This is another item in the other list")
                    item("Big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, line!)")
                }

                list(numbered(8)) {
                    style(colour = Color.RED) {
                        item("This is yet another list")
                    }
                    style(colour = Color.GREEN) {
                        item("This is yet another item in the other list")
                    }
                    style(colour = Color.BLUE) {
                        item("Yet another Big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, big, line!)")
                    }
                    style(colour = Color.BLACK) {
                        item {
                            +"I can even put a table here..."
                        }.table(3) {
                            textCell("A")
                            textCell("B")
                            textCell("C")
                            textCell("D")
                            textCell("E")
                        }
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `list with item containing a table`(approver: PdfApprover) {
        content {
            list {
                item("Item 1")
                item("Item 2").table(3) {
                    textCell("1")
                    textCell("2")
                    textCell("3")
                    textCell("4")
                }.list {
                    item("Sub list under table 1")
                    item("Sub list under table 2")
                }
                item("Item 3")
            }
        }.approve(approver)
    }
}