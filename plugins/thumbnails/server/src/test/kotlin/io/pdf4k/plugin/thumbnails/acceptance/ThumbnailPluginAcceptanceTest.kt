package io.pdf4k.plugin.thumbnails.acceptance

import io.pdf4k.domain.HorizontalAlignment
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.StyleAttributes.Companion.style
import io.pdf4k.plugin.thumbnails.dsl.thumbnail
import io.pdf4k.server.testing.AbstractServerTest
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test

class ThumbnailPluginAcceptanceTest : AbstractServerTest() {
    @Test
    fun `render thumbnail of image`(approver: PdfApprover) = with(emptyScenario()) {
        operator.rendersAPdfImmediately {
            page {
                content {
                    table(1, style(align = HorizontalAlignment.Center), widthPercentage = 53f) {
                        thumbnail(resource = local("beach.png"), width = 300, height = 200)
                        textCell {
                            +">> "
                            thumbnail(resource = local("beach.png"), width = 50, height = 30)
                            +" <<"
                        }
                    }
                }
            }
        }.approve(approver)
    }
}