package io.pdf4k.approval

import io.pdf4k.domain.Font.Style.Bold
import io.pdf4k.domain.HorizontalAlignment.*
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.domain.StyleAttributes.Companion.noBorder
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.dsl.StationaryBuilder.Companion.plusBlocks
import io.pdf4k.dsl.StationaryBuilder.Companion.withBlocks
import io.pdf4k.testing.AbstractPdfRendererTest
import io.pdf4k.testing.PdfApprover
import io.pdf4k.testing.domain.Musician.Companion.musicians
import io.pdf4k.testing.extensions.splitParagraphs
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.Color.RED

class BlocksTest : AbstractPdfRendererTest() {
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

    @Test
    fun `renders pages with 2 column text`(approver: PdfApprover) {
        pdf {
            page(stationary = twoColumns) {
                content(style(size = 24f, align = JustifiedAll)) {
                    +"""
                        Within seconds he ran out onto the deck and waved and grinned at over
                        three billion people. The three billion people weren’t actually there, but they
                        watched his every gesture through the eyes of a small robot tri-D camera
                        which hovered obsequiously in the air nearby. The antics of the President
                        always made amazingly popular tri-D; that’s what they were for.
                        
                        He grinned again. Three billion and six people didn’t know it, but today
                        would be a bigger antic than anyone had bargained for.
                        
                        The robot camera homed in for a close up on the more popular of his two
                        heads and he waved again. He was roughly humanoid in appearance except
                        for the extra head and third arm. His fair tousled hair stuck out in random
                        directions, his blue eyes glinted with something completely unidentifiable,
                        and his chins were almost always unshaven.
                        
                        A twenty-foot-high transparent globe floated next to his boat, rolling and
                        bobbing, glistening in the brilliant sun. Inside it floated a wide semi-circular
                        sofa upholstered in glorious red leather: the more the globe bobbed and
                        rolled, the more the sofa stayed perfectly still, steady as an upholstered rock.
                        Again, all done for effect as much as anything.
                        
                        Zaphod stepped through the wall of the globe and relaxed on the sofa.
                        He spread his two arms lazily along the back and with the third brushed
                        some dust off his knee. His heads looked about, smiling; he put his feet up.
                        At any moment, he thought, he might scream.
                        
                        Water boiled up beneath the bubble, it seethed and spouted. The bubble
                        surged into the air, bobbing and rolling on the water spout. Up, up it
                        climbed, throwing stilts of light at the cliff. Up it surged on the jet, the
                        water falling from beneath it, crashing back into the sea hundreds of feet
                        below.
                    """.splitParagraphs().joinToString("")
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders artists in two columns`(approver: PdfApprover) {
        pdf {
            page(stationary = twoColumns) {
                content(noBorder) {
                    table {
                        musicians.forEach { musician ->
                            tableCell(1, style(align = Center)) {
                                textCell(musician.name, style(fontStyle = Bold, size = 16f))
                                style(paddingTop = 16f, paddingBottom = 16f) {
                                    imageCell("musicians/${musician.image}", width = 200f, height = 200f)
                                }
                            }
                            textCell(musician.bio, style(size = 12f, align = Justified))
                            textCell { crlf() }
                        }
                    }
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders a block break`(approver: PdfApprover) {
        pdf {
            page(stationary = twoColumns) {
                content(style(size = 24f, align = JustifiedAll)) {
                    +"""
                        Within seconds he ran out onto the deck and waved and grinned at over
                        three billion people. The three billion people weren’t actually there, but they
                        watched his every gesture through the eyes of a small robot tri-D camera
                        which hovered obsequiously in the air nearby. The antics of the President
                        always made amazingly popular tri-D; that’s what they were for.
                        
                        He grinned again. Three billion and six people didn’t know it, but today
                        would be a bigger antic than anyone had bargained for.
                    """.splitParagraphs().joinToString("")

                    blockBreak()

                    +"""
                        The robot camera homed in for a close up on the more popular of his two
                        heads and he waved again. He was roughly humanoid in appearance except
                        for the extra head and third arm. His fair tousled hair stuck out in random
                        directions, his blue eyes glinted with something completely unidentifiable,
                        and his chins were almost always unshaven.
                     """.splitParagraphs().joinToString("")

                    blockBreak()

                    +"""
                        A twenty-foot-high transparent globe floated next to his boat, rolling and
                        bobbing, glistening in the brilliant sun. Inside it floated a wide semi-circular
                        sofa upholstered in glorious red leather: the more the globe bobbed and
                        rolled, the more the sofa stayed perfectly still, steady as an upholstered rock.
                        Again, all done for effect as much as anything.
                    """.trimIndent()
                }
            }
        }.approve(approver)
    }

    @Test
    fun `renders a page break`(approver: PdfApprover) {
        pdf {
            page(stationary = twoColumns) {
                content(style(size = 24f, align = JustifiedAll)) {
                    +"""
                        Within seconds he ran out onto the deck and waved and grinned at over
                        three billion people. The three billion people weren’t actually there, but they
                        watched his every gesture through the eyes of a small robot tri-D camera
                        which hovered obsequiously in the air nearby. The antics of the President
                        always made amazingly popular tri-D; that’s what they were for.
                        
                        He grinned again. Three billion and six people didn’t know it, but today
                        would be a bigger antic than anyone had bargained for.
                    """.splitParagraphs().joinToString("")

                    pageBreak()

                    +"""The robot camera homed in for a close up on the more popular of his two
                        heads and he waved again. He was roughly humanoid in appearance except
                        for the extra head and third arm. His fair tousled hair stuck out in random
                        directions, his blue eyes glinted with something completely unidentifiable,
                        and his chins were almost always unshaven.
                     """.splitParagraphs().joinToString("")

                    pageBreak()
                    pageBreak()

                    +"Last page"
                }
            }
        }.approve(approver)
    }

    @Test
    fun `sets a background image for a block`(approver: PdfApprover) {
        pdf {
            page(stationary = fourColumns, style = style(size = 24f, align = JustifiedAll, background = Color(255, 255, 255, 128))) {
                block("col1", backgroundImage = local("port.png")) {}
                block("col4", backgroundImage = local("needles.png")) {}
                content {
                    +"""
                        Within seconds he ran out onto the deck and waved and grinned at over
                        three billion people. The three billion people weren’t actually there, but they
                        watched his every gesture through the eyes of a small robot tri-D camera
                        which hovered obsequiously in the air nearby. The antics of the President
                        always made amazingly popular tri-D; that’s what they were for.
                        
                        He grinned again. Three billion and six people didn’t know it, but today
                        would be a bigger antic than anyone had bargained for.
                        
                        The robot camera homed in for a close up on the more popular of his two
                        heads and he waved again. He was roughly humanoid in appearance except
                        for the extra head and third arm. His fair tousled hair stuck out in random
                        directions, his blue eyes glinted with something completely unidentifiable,
                        and his chins were almost always unshaven.
                        
                        A twenty-foot-high transparent globe floated next to his boat, rolling and
                        bobbing, glistening in the brilliant sun. Inside it floated a wide semi-circular
                        sofa upholstered in glorious red leather: the more the globe bobbed and
                        rolled, the more the sofa stayed perfectly still, steady as an upholstered rock.
                        Again, all done for effect as much as anything.
                        
                        Zaphod stepped through the wall of the globe and relaxed on the sofa.
                        He spread his two arms lazily along the back and with the third brushed
                        some dust off his knee. His heads looked about, smiling; he put his feet up.
                        At any moment, he thought, he might scream.
                        
                        Water boiled up beneath the bubble, it seethed and spouted. The bubble
                        surged into the air, bobbing and rolling on the water spout. Up, up it
                        climbed, throwing stilts of light at the cliff. Up it surged on the jet, the
                        water falling from beneath it, crashing back into the sea hundreds of feet
                        below
                    """.trimIndent()
                }
            }
        }.approve(approver)
    }

    companion object {
        private val stationaryWithBlock = BlankA4Portrait.plusBlocks {
            block("blockName", 115f, 520f, 450f, 100f)
        }

        val twoColumns = BlankA4Portrait.withBlocks {
            block("col1", 24f, 24f, 262f, 796f)
            block("col2", 310f, 24f, 262f, 796f)
            contentFlow("col1", "col2")
        }

        val fourColumns = BlankA4Portrait.withBlocks {
            block("col1", 24f, 434f, 262f, 386f)
            block("col2", 310f, 434f, 262f, 386f)
            block("col3", 24f, 24f, 262f, 386f)
            block("col4", 310f, 24f, 262f, 386f)
            contentFlow("col1", "col2", "col3", "col4")
        }
    }
}