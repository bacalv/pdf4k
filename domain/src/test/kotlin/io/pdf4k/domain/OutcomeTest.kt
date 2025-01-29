package io.pdf4k.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class OutcomeTest {
    @Test
    fun `get or null on a Success`() {
        assertEquals("NOT NULL", Outcome.Success<String, Any>("NOT NULL").getOrNull())
    }

    @Test
    fun `get or null on a Failure`() {
        assertNull(Outcome.Failure<Any, String>("ERROR").getOrNull())
    }
}