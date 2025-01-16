package io.pdf4k.renderer

import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.testing.AbstractPdfApproverTest
import org.junit.jupiter.api.Test
import io.pdf4k.testing.PdfApprover
import java.awt.Color.BLUE

class LinksTest : AbstractPdfApproverTest() {
    @Test
    fun `adds a link to another part of the document`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    paragraph { anchor("page-1", "This is page 1") }
                    crlf()
                    paragraph { link("#page-2", "Go to page 2", linkStyle) }
                }
            }
            page {
                content {
                    paragraph { anchor("page-2", "This is page 2") }
                    crlf()
                    paragraph { link("#page-3", "Go to page 3", linkStyle) }
                }
            }
            page {
                content {
                    paragraph { anchor("page-3", "This is page 3") }
                    crlf()
                    paragraph { link("#page-1", "Go to page 1", linkStyle) }
                    crlf()
                    crlf()
                    paragraph { link("https://github.com", "Go to Github", linkStyle) }
                }
            }
        }.approve(approver)
    }

    companion object {
        private val linkStyle = style(underlined = true, colour = BLUE)
    }
}