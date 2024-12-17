package io.pdf4k.renderer

import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.dsl.StationaryBuilder.Companion.withBlocks
import io.pdf4k.testing.AbstractPdfApproverTest
import org.junit.jupiter.api.Test
import pro.juxt.pdf4k.testing.PdfApprover
import java.awt.Color.RED

class BlocksTest : AbstractPdfApproverTest() {
    @Test
    fun `render a paragraph in a block`(approver: PdfApprover) {
        pdf {
            page(stationary = stationaryWithBlock) {
                block("blockName") {
                    +"Text in a block"
                }
            }
        }.approve(approver)
    }

    @Test
    fun `render a paragraph in a block and main content`(approver: PdfApprover) {
        pdf {
            page(stationary = stationaryWithBlock) {
                block("blockName") {
                    +"Text in a block"
                }
                content {
                    +"Main content"
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders page numbers in blocks only`(approver: PdfApprover) {
        pdf {
            repeat(3) {
                page(stationary = stationaryWithBlock) {
                    block("blockName", style(colour = RED)) {
                        paragraph {
                            +"This is page "
                            pageNumber()
                        }
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders a table in a block`(approver: PdfApprover) {
        pdf {
            page(stationary = stationaryWithBlock) {
                block("blockName") {
                    table(3) {
                        textCell("Col1")
                        textCell("Col2")
                        tableCell(2) {
                            textCell("This is page")
                            textCell { pageNumber() }
                        }
                    }
                }
            }
        }.approve(approver)
    }

    companion object {
        private val stationaryWithBlock = BlankA4Portrait.withBlocks {
            block("blockName", 115f, 520f, 450f, 100f)
        }
    }
}