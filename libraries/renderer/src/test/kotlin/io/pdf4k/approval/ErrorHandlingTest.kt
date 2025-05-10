package io.pdf4k.approval

import io.pdf4k.domain.Font
import io.pdf4k.domain.PdfError.*
import io.pdf4k.domain.ResourceLocation.Companion.custom
import io.pdf4k.domain.ResourceLocation.Companion.local
import io.pdf4k.domain.ResourceLocation.Local
import io.pdf4k.domain.ResourceLocation.Remote.Custom
import io.pdf4k.domain.named
import io.pdf4k.domain.stringValue
import io.pdf4k.domain.toArgument
import io.pdf4k.dsl.PdfBuilder.Companion.pdf
import io.pdf4k.dsl.StationaryBuilder.Companion.stationary
import io.pdf4k.provider.KeyProvider.Companion.toPrivateKey
import io.pdf4k.testing.InMemoryRenderer.render
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ErrorHandlingTest {
    @Test
    fun `page template not found`() {
        assertThrows<PageTemplateNotFound> {
            pdf {
                section(stationary = stationary("not_found", 1, 1f, 1f))
            }.render()
        }.let { error ->
            assertEquals(local("not_found"), error.resource)
        }
    }

    @Test
    fun `key parse error`() {
        assertThrows<KeyParseError> { toPrivateKey("RUBBISH") }
        assertThrows<KeyParseError> { toPrivateKey("RUBBISH\n") }
        assertThrows<KeyParseError> { toPrivateKey("-----BEGIN PRIVATE KEY-----\n") }
        assertThrows<KeyParseError> { toPrivateKey("-----BEGIN PRIVATE KEY-----\n-----END PRIVATE KEY-----") }
    }

    @Test
    fun `font not found`() {
        assertThrows<FontNotFound> {
            pdf {
                section {
                    content {
                        style(font = Font.Resource(local("not_found"), "not_found")) {
                            +"Hello"
                        }
                    }
                }
            }.render()
        }.let { error ->
            assertEquals(FontNotFound(local("not_found")), error)
        }
    }

    @Test
    fun `image not found`() {
        assertThrows<ImageNotFound> {
            pdf {
                section {
                    content {
                        paragraph {
                            image("not_found")
                        }
                    }
                }
            }.render()
        }.let { error ->
            assertEquals("not_found", (error.resource as Local).name)
        }
    }

    @Test
    fun `unknown custom image provider`() {
        assertThrows<CustomResourceProviderNotFound> {
            pdf {
                section {
                    content {
                        table {
                            imageCell(custom("not_found"))
                        }
                    }
                }
            }.render()
        }.let { error ->
            assertEquals("not_found", error.providerName)
        }
    }

    @Test
    fun `custom provider image not found`() {
        assertThrows<ImageNotFound> {
            pdf {
                section {
                    content {
                        table {
                            imageCell(custom("custom", "not_found".toArgument("location")))
                        }
                    }
                }
            }.render()
        }.let { error ->
            assertEquals("custom", (error.resource as Custom).providerName)
            assertEquals("not_found", (error.resource as Custom).arguments.named("location").stringValue())
        }
    }
}