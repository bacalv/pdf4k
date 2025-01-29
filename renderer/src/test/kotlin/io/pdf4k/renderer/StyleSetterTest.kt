package io.pdf4k.renderer

import com.lowagie.text.pdf.PdfPCell
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pdf4k.domain.StyleAttributes
import org.junit.jupiter.api.Test

class StyleSetterTest {
    @Test
    fun `set cell style to defaults`() {
        val cell = mockk<PdfPCell>(relaxed = true)
        val style = StyleAttributes()
        val context = mockk<RendererContext>()

        every { context.peekStyle() }.returns(style)

        with(StyleSetter) {
            cell.setStyle(context)
        }

        verify { cell.horizontalAlignment = 0 }
    }
}