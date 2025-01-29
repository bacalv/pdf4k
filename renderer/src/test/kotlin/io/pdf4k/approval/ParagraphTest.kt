package io.pdf4k.approval

import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.HorizontalAlignment
import io.pdf4k.domain.Leading
import io.pdf4k.domain.Leading.Companion.fixed
import io.pdf4k.domain.Leading.Companion.leading
import io.pdf4k.domain.Leading.Companion.multiplier
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.PdfBuilder.Companion.content
import io.pdf4k.extensions.singleLine
import io.pdf4k.extensions.splitParagraphs
import io.pdf4k.testing.AbstractPdfApproverTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class ParagraphTest : AbstractPdfApproverTest() {
    @Test
    fun `render simple paragraph`(approver: PdfApprover) {
        content {
            +"Simple Paragraph"
        }.approve(approver)
    }

    @Test
    fun `render long paragraph`(approver: PdfApprover) {
        content {
            +"""
                Bypasses are devices which allow some people to drive from point A to
                point B very fast whilst other people dash from point B to point A very fast.
                People living at point C, being a point directly in between, are often given
                to wonder what’s so great about point A that so many people of point B are
                so keen to get there, and what’s so great about point B that so many people
                of point A are so keen to get there. They often wish that people would just
                once and for all work out where the hell they wanted to be.
            """.singleLine()
        }.approve(approver)
    }

    @Test
    fun `render two phrases in a paragraph`(approver: PdfApprover) {
        content {
            paragraph {
                +"Phrase 1"
                +"Phrase 2"
            }
        }.approve(approver)
    }

    @Test
    fun `render two phrases in two paragraphs`(approver: PdfApprover) {
        content {
            phrase("Phrase 1")
            phrase("Phrase 2")
        }.approve(approver)
    }

    @Test
    fun `render chunks`(approver: PdfApprover) {
        content {
            paragraph {
                phrase {
                    +"Chunk 1"
                    +"Chunk 2"
                }
            }
        }.approve(approver)
    }

    @ParameterizedTest
    @MethodSource("leading")
    fun `render paragraph with different text sizes and leading`(leading: Leading, approver: PdfApprover) {
        content {
            paragraph(style(size = 12f, leading = leading)) {
                +"""
                    The island of France was about twenty miles long, five miles across the
                    middle, sandy and crescent shaped. In fact it seemed to exist not so much as
                    an island in its own right as simply a means of defining the sweep and curve
                    of a huge bay. This impression was 
                """.singleLine()
                "heightened" and style(size = 24f, fontStyle = Bold)
                +" "
                +"""
                    by the fact that the inner
                    coastline of the crescent consisted almost entirely of steep cliffs. From the
                    top of the cliff the land sloped slowly down five miles to the opposite shore.
                    On top of the cliffs stood a reception committee.
                """.singleLine()
            }
        }.approve(approver)
    }

    @Test
    fun `render paragraphs with horizontal alignment`(approver: PdfApprover) {
        content {
            paragraph(style(align = HorizontalAlignment.Left)) { +"Left aligned" }
            paragraph(style(align = HorizontalAlignment.Right)) { +"Right aligned" }
            paragraph(style(align = HorizontalAlignment.Justified)) {
                crlf()
                "Justified." and style(fontStyle = Bold)
                crlf()
                crlf()
                +"""
                    A twenty-foot-high transparent globe floated next to his boat, rolling and
                    bobbing, glistening in the brilliant sun. Inside it floated a wide semi-circular
                    sofa upholstered in glorious red leather: the more the globe bobbed and
                    rolled, the more the sofa stayed perfectly still, steady as an upholstered rock.
                    Again, all done for effect as much as anything.
                """.singleLine()
            }
            paragraph(style(align = HorizontalAlignment.JustifiedAll)) {
                crlf()
                "Justified all." and style(fontStyle = Bold)
                crlf()
                crlf()
                +"""
                    Zaphod stepped through the wall of the globe and relaxed on the sofa.
                    He spread his two arms lazily along the back and with the third brushed
                    some dust off his knee. His heads looked about, smiling; he put his feet up.
                    At any moment, he thought, he might scream.
                """.singleLine()
            }
        }.approve(approver)
    }

    @Test
    fun `renders unicode text`(approver: PdfApprover) {
        content {
            style(splitLate = true, splitRows = false) {
                """
                    Water boiled up beneath the bubble, it seethed and spouted. The bubble
                    surged into the air, bobbing and rolling on the water spout. Up, up it
                    climbed, throwing stilts of light at the cliff. Up it surged on the jet, the
                    water falling from beneath it, crashing back into the sea hundreds of feet
                    below
    
                    水在气泡下面沸腾，它沸腾着喷涌而出。泡沫 冲向空中，在喷水口上晃动滚动。向上，向上 爬上去，
                    向悬崖投掷光高跷。它在喷气式飞机上汹涌而上， 水从它下面落下，撞回数百英尺高的海中 下面
    
                    El agua hirvió debajo de la burbuja, hirvió y brotó. La burbuja se elevó en el aire,
                    moviéndose y rodando sobre el chorro de agua. Subió, subió, arrojando zancos de luz 
                    al acantilado. Subió sobre el chorro, el agua cayó desde debajo de ella, estrellándose 
                    de nuevo contra el mar a cientos de pies debajo
                """.splitParagraphs().map(::paragraph)
            }
        }.approve(approver)
    }

    companion object {
        @JvmStatic
        fun leading() = listOf(
            arguments(multiplier(1f)),
            arguments(multiplier(1.2f)),
            arguments(fixed(18f)),
            arguments(fixed(28f)),
            arguments(leading(12f, 0.5f)),
        )
    }
}