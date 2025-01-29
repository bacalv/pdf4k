package io.pdf4k.approval

import io.pdf4k.approval.InMemoryRenderer.render
import io.pdf4k.domain.Font
import io.pdf4k.domain.Outcome
import io.pdf4k.domain.Outcome.Failure
import io.pdf4k.domain.Outcome.Success
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.dsl.StationaryBuilder.Companion.stationary
import io.pdf4k.renderer.KeyProvider.Companion.toPrivateKey
import io.pdf4k.renderer.PdfError
import io.pdf4k.renderer.PdfError.KeyParseError
import io.pdf4k.renderer.PdfError.PageTemplateNotFound
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ErrorHandlingTest {
    @Test
    fun `page template not found`() {
        assertError<PageTemplateNotFound> {
            pdf {
                page(stationary = stationary("not_found", 1, 1f, 1f))
            }.render()
        }.let { error ->
            assertEquals("not_found", error.templateName)
        }
    }

    @Test
    fun `key parse error`() {
        assertError<KeyParseError> { toPrivateKey("RUBBISH") }
        assertError<KeyParseError> { toPrivateKey("RUBBISH\n") }
        assertError<KeyParseError> { toPrivateKey("-----BEGIN PRIVATE KEY-----\n") }
        assertError<KeyParseError> { toPrivateKey("-----BEGIN PRIVATE KEY-----\n-----END PRIVATE KEY-----") }
    }

    @Disabled
    @Test
    fun `font not found`() {
        assertError<PageTemplateNotFound> {
            pdf {
                page {
                    content {
                        style(font = Font.Custom.Resource("not_found")) {
                            +"Hello"
                        }
                    }
                }
            }.render()
        }.let { error ->
            assertEquals("not_found", error.templateName)
        }
    }

    companion object {
        inline fun <reified E : PdfError> assertError(block: () -> Outcome<*, *>): E = when (val outcome = block()) {
            is Success -> fail("Expected a failure")
            is Failure -> {
                assertInstanceOf(E::class.java, outcome.error).let { outcome.error }
                outcome.error as E
            }
        }
    }
}