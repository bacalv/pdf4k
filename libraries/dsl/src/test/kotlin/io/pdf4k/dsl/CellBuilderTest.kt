package io.pdf4k.dsl

import io.mockk.mockk
import io.pdf4k.domain.Margin
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CellBuilderTest {
    @Test
    fun `throws exception if child builder returns an unexpected element`() {
        val badBuilder = mockk<ComponentBuilder<*, *>>()
        val builder = CellBuilder(1, 1, Margin.ZERO, badBuilder)
        val exception = assertThrows<IllegalStateException> { builder.build() }
        assertTrue(exception.message?.startsWith("Incompatible child builder of type ComponentBuilder$") == true)
    }
}