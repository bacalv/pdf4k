package io.pdf4k.approval

import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.dsl.StationaryBuilder.Companion.plusBlocks
import io.pdf4k.testing.AbstractPdfRendererTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import java.awt.Color.BLUE

class LinksTest : AbstractPdfRendererTest() {
    @Test
    fun `adds a link to another part of the document`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    paragraph { anchor("page-1", "This is page 1") }
                    paragraph { link("#page-2", "Go to page 2", linkStyle) }
                }
            }
            page {
                content {
                    paragraph { anchor("page-2", "This is page 2") }
                    paragraph { link("#page-3", "Go to page 3", linkStyle) }
                }
            }
            page {
                content {
                    paragraph { anchor("page-3", "This is page 3") }
                    paragraph { link("#page-1", "Go to page 1", linkStyle) }
                    paragraph { link("https://github.com", "Go to Github", linkStyle) }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `adds a link from a block to main content`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    paragraph { anchor("page-1", "This is page 1") }
                    paragraph { link("#page-2", "Go to page 2", linkStyle) }
                }
            }
            page(stationary = stationaryWithBlock) {
                block("blockName") {
                    paragraph { anchor("page-2", "This is page 2") }
                    paragraph { link("#page-1", "Go to page 1", linkStyle) }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `adds a link at the paragraph level`(approver: PdfApprover) {
        content {
            paragraph {
                link("https://github.com") {
                    +"Hello "
                    "Github!" and style(fontStyle = Bold)
                }
            }
        }.approve(approver)
    }

    companion object {
        private val stationaryWithBlock = BlankA4Portrait.plusBlocks {
            block("blockName", 115f, 520f, 450f, 100f)
        }

        private val linkStyle = style(underlined = true, colour = BLUE)
    }
}