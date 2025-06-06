package io.pdf4k.renderer

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pdf4k.domain.LoadedStationary
import io.pdf4k.domain.ResourceLocation.Local
import io.pdf4k.domain.Section
import io.pdf4k.domain.Stationary.Companion.BlankA4Portrait
import io.pdf4k.dsl.StationaryBuilder.Companion.withBlocks
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SectionEventListenerTest {
    @Test
    fun `throws exception if loaded stationary not found on set current page template`() {
        val context = mockk<RendererContext>()
        val listener = PageEventListener(context)
        val section = Section(listOf(BlankA4Portrait), style = null, content = mockk(), blockContent = mockk(), backgroundImages = mockk())

        every { context.loadedStationary }.returns(emptyMap())

        val exception = assertThrows<IllegalStateException> { listener.setCurrentSection(section) }
        assertEquals("Cannot find loaded stationary for template '${Local("blank-a4-portrait")}'", exception.message)
    }

    @Test
    fun `throws exception if block not found on set current page template`() {
        val context = mockk<RendererContext>(relaxed = true)
        val listener = PageEventListener(context)
        val stationary = BlankA4Portrait.withBlocks { }
        val section = Section(listOf(stationary), style = null, content = mockk(), blockContent = mockk(), backgroundImages = mockk())

        every { context.loadedStationary }.returns(mapOf(stationary to LoadedStationary(stationary, mockk())))
        every { context.mainDocument }.returns(mockk(relaxed = true))

        val exception = assertThrows<IllegalStateException> { listener.setCurrentSection(section) }
        assertEquals("Cannot find block #0 for template '${Local("blank-a4-portrait")}'", exception.message)
    }

    @Test
    fun `adds new page on close if not all content blocks are filled`() {
        val context = mockk<RendererContext>(relaxed = true)
        val listener = PageEventListener(context)
        val stationary = BlankA4Portrait.withBlocks {
            block("1", 100f, 100f, 100f, 100f)
            block("2", 100f, 100f, 100f, 100f)
            contentFlow("1", "2")
        }
        val section = Section(listOf(stationary), style = null, content = mockk(), blockContent = mockk(), backgroundImages = mockk())

        every { context.loadedStationary }.returns(mapOf(stationary to LoadedStationary(stationary, mockk())))
        every { context.mainDocument }.returns(mockk(relaxed = true))

        listener.setCurrentSection(section)
        listener.onStartPage(mockk(), mockk())
        listener.close()

        verify { context.nextPage(stationary, 1) }
    }

    @Test
    fun `adds new page on close if not all content blocks are filled on overflow page`() {
        val context = mockk<RendererContext>(relaxed = true)
        val listener = PageEventListener(context)
        val stationary = BlankA4Portrait.withBlocks {
            block("1", 100f, 100f, 100f, 100f)
            block("2", 100f, 100f, 100f, 100f)
            contentFlow("1", "2")
        }
        val section = Section(listOf(stationary), style = null, content = mockk(), blockContent = mockk(), backgroundImages = mockk())

        every { context.loadedStationary }.returns(mapOf(stationary to LoadedStationary(stationary, mockk())))
        every { context.mainDocument }.returns(mockk(relaxed = true))

        listener.setCurrentSection(section)
        listener.onStartPage(mockk(), mockk())
        listener.onStartPage(mockk(), mockk())
        listener.onStartPage(mockk(), mockk())
        listener.close()

        verify { context.nextPage(stationary, 1) }
    }

    @Test
    fun `do nothing on close if block count is 0`() {
        val context = mockk<RendererContext>(relaxed = true)
        val listener = PageEventListener(context)
        val stationary = BlankA4Portrait.withBlocks {
            block("1", 100f, 100f, 100f, 100f)
            block("2", 100f, 100f, 100f, 100f)
            contentFlow("1", "2")
        }
        val section = Section(listOf(stationary), style = null, content = mockk(), blockContent = mockk(), backgroundImages = mockk())

        every { context.loadedStationary }.returns(mapOf(stationary to LoadedStationary(stationary, mockk())))
        every { context.mainDocument }.returns(mockk(relaxed = true))

        listener.setCurrentSection(section)
        listener.close()

        verify(exactly = 0) { context.nextPage(stationary, 1) }
    }
}