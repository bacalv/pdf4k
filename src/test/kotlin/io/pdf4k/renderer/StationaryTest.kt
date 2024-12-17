package io.pdf4k.renderer

import io.pdf4k.domain.Font
import io.pdf4k.domain.HorizontalAlignment.Justified
import io.pdf4k.domain.HorizontalAlignment.Right
import io.pdf4k.domain.Margin
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.dsl.StationaryBuilder.Companion.stationary
import io.pdf4k.extensions.splitParagraphs
import io.pdf4k.testing.AbstractPdfApproverTest
import org.junit.jupiter.api.Test
import pro.juxt.pdf4k.testing.PdfApprover

class StationaryTest : AbstractPdfApproverTest() {
    @Test
    fun `render empty page`(approver: PdfApprover) {
        pdf {
            page()
        }.approve(approver)
    }

    @Test
    fun `render text on blank A4 portrait`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    +"Hello World!"
                }
            }
        }.approve(approver)
    }

    @Test
    fun `render text on 2 pages`(approver: PdfApprover) {
        pdf {
            repeat(2) {
                page {
                    content {
                        +"Page ${it + 1}"
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `can adjust margin`(approver: PdfApprover) {
        pdf {
            page(stationary = BlankA4DifferentMargins) {
                content {
                    +"Hello World!"
                }
            }
        }.approve(approver)
    }

    @Test
    fun `can use different stationary for second page`(approver: PdfApprover) {
        pdf {
            page {
                content {
                    +"Page 1"
                }
            }
            page(stationary = BlankA4DifferentMargins) {
                content {
                    +"Page 2 - margin changed"
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders content on an existing PDF page template`(approver: PdfApprover) {
        pdf {
            page(stationary = Letter, continuation = arrayOf(ContinuationPage)) {
                block("address") {
                    +"""
                        Mr Arthur Dent
                        123 Fake Street
                        Faketon,
                        Fakeshire
                        FA1 K3E
                    """.trimIndent()
                }

                content(style(align = Justified)) {
                    paragraph(style(align = Right)) {
                        +"23rd September, 2025"
                    }
                    paragraph {
                        "RE: NOTICE OF NEW BYPASS" and style(fontStyle = Font.Style.Bold)
                    }
                    crlf()
                    paragraph("Dear Mr Dent")
                    crlf()
                    """
                        The house stood on a slight rise just on the edge of the village. It stood on
                        its own and looked over a broad spread of West Country farmland. Not a
                        remarkable house by any means – it was about thirty years old, squattish,
                        squarish, made of brick, and had four windows set in the front of a size and
                        proportion which more or less exactly failed to please the eye.
                        
                        The only person for whom the house was in any way special was Arthur
                        Dent, and that was only because it happened to be the one he lived in. He
                        had lived in it for about three years, ever since he had moved out of London
                        because it made him nervous and irritable. He was about thirty as well,
                        dark haired and never quite at ease with himself. The thing that used to
                        worry him most was the fact that people always used to ask him what he
                        was looking so worried about. He worked in local radio which he always used
                        to tell his friends was a lot more interesting than they probably thought. It
                        was, too – most of his friends worked in advertising.
                        
                        On Wednesday night it had rained very heavily, the lane was wet and
                        muddy, but the Thursday morning sun was bright and clear as it shone on
                        Arthur Dent’s house for what was to be the last time
                        
                        It hadn’t properly registered with Arthur that the council wanted to
                        knock down his house and build an bypass instead.
                        
                        At eight o’clock on Thursday morning Arthur didn’t feel very good. He
                        woke up blearily, got up, wandered blearily round his room, opened a window,
                        saw a bulldozer, found his slippers, and stomped off to the bathroom to wash.
                        
                        Toothpaste on the brush – so. Scrub.
                        
                        Shaving mirror – pointing at the ceiling. He adjusted it. For a moment
                        it reflected a second bulldozer through the bathroom window. Properly adjusted, 
                        it reflected Arthur Dent’s bristles. He shaved them off, washed, dried,
                        and stomped off to the kitchen to find something pleasant to put in his mouth.
                        
                        Kettle, plug, fridge, milk, coffee. Yawn.
                        
                        The word bulldozer wandered through his mind for a moment in search of something 
                        to connect with.
                        
                        The bulldozer outside the kitchen window was quite a big one.
                        
                        He stared at it. ”Yellow,” he thought and stomped off back to his bedroom
                        to get dressed.
                        
                        Passing the bathroom he stopped to drink a large glass of water, and
                        another. He began to suspect that he was hung over. Why was he hung
                        over? Had he been drinking the night before? He supposed that he must
                        have been. He caught a glint in the shaving mirror. ”Yellow,” he thought
                        and stomped on to the bedroom.
                        
                        He stood and thought. The pub, he thought. Oh dear, the pub. He
                        vaguely remembered being angry, angry about something that seemed important. 
                        He’d been telling people about it, telling people about it at great
                        length, he rather suspected: his clearest visual recollection was of glazed
                        looks on other people’s faces. Something about a new bypass he had just
                        found out about. It had been in the pipeline for months only no one seemed
                        to have known about it. Ridiculous. He took a swig of water. It would sort
                        itself out, he’d decided, no one wanted a bypass, the council didn’t have a
                        leg to stand on. It would sort itself out.
                        
                        Yours Sincerely,
                        
                        The Local Council
                    """.splitParagraphs().map(::paragraph)
                }
            }
        }.approve(approver)
    }

    companion object {
        val BlankA4DifferentMargins = BlankA4Portrait.copy(margin = Margin(100f, 100f, 100f, 100f))
        val Letter = stationary("letter", 1, Margin(250f, 42f, 46f, 30f)) {
            block("address", 50f, 600f, 120f, 120f)
        }
        val ContinuationPage = stationary("continuation-page", 1, Margin.DEFAULT_MARGIN.copy(top = 120f))
    }
}