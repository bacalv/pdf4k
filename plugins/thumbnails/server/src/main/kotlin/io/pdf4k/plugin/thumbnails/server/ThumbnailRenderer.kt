package io.pdf4k.plugin.thumbnails.server

import io.pdf4k.domain.Argument
import io.pdf4k.domain.intValue
import io.pdf4k.domain.named
import io.pdf4k.domain.resourceValue
import io.pdf4k.plugin.thumbnails.domain.thumbnailsProviderName
import io.pdf4k.provider.CustomResourceProvider
import io.pdf4k.provider.ResourceLocator
import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ThumbnailRenderer : CustomResourceProvider {
    override val name: String = thumbnailsProviderName
    override fun load(arguments: List<Argument>, resourceLocator: ResourceLocator): InputStream {
        val resource = arguments.named("resource").resourceValue()
        val width = arguments.named("width").intValue()
        val height = arguments.named("height").intValue()

        return ByteArrayOutputStream().also { output ->
            val sourceImage = resourceLocator.load(resource)
            Thumbnails.of(sourceImage)
                .width(width)
                .height(height)
                .toOutputStream(output)
        }.let {
            ByteArrayInputStream(it.toByteArray())
        }
    }
}