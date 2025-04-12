package io.pdf4k.server.provider

import io.pdf4k.domain.*
import io.pdf4k.provider.CustomResourceProvider
import io.pdf4k.provider.TempFileFactory
import io.pdf4k.server.domain.LocalResource
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class ResourceDownloader(
    private val tempFileFactory: TempFileFactory,
    private val httpClient: HttpHandler,
    private val customProviders: Map<String, CustomResourceProvider>,
    private val loadResource: (ResourceType, String) -> InputStream?
) {
    fun downloadResources(pdf: Pdf): Map<ResourceLocation, LocalResource> {
        val locations = mutableSetOf<ResourceLocation>()
        findResources(pdf, locations)
        val downloaded = mutableMapOf<ResourceLocation, LocalResource>()
        locations.forEach { location ->
            downloadResource(location, downloaded)
        }
        return TODO()
    }

    private fun downloadResource(location: ResourceLocation, downloaded: MutableMap<ResourceLocation, LocalResource>) {
        if (!downloaded.containsKey(location)) {
            when (location) {
                is ResourceLocation.Local -> downloaded[location] = LocalResource.Classpath
                is ResourceLocation.Remote.Custom -> {
                    location.arguments.filterIsInstance<ResourceLocation>().forEach {
                        downloadResource(it, downloaded)
                    }
                    val provider = customProviders[location.providerName] ?: throw PdfError.CustomResourceProviderNotFound(location.providerName)
                    provider.load(location.arguments, { resource ->
                        val downloadedResource = downloaded[resource] ?: TODO()
                        when (downloadedResource) {
                            LocalResource.Classpath -> loadResource(ResourceType.Image, (resource as ResourceLocation.Local).name)
                                ?: throw PdfError.ClasspathResourceNotFound(resource.name)
                            is LocalResource.DownloadedFile -> FileInputStream(downloadedResource.file)
                        }
                    })?.let { loaded ->
                        val file = tempFileFactory.createTempFile().toFile()
                        FileOutputStream(file).use {
                            loaded.copyTo(it)
                        }
                        downloaded[location] = LocalResource.DownloadedFile(file)
                    }
                }
                is ResourceLocation.Remote.Uri -> httpClient(Request(Method.GET, location.uri.toASCIIString())).use {
                    val file = tempFileFactory.createTempFile().toFile()
                    FileOutputStream(file).use { stream -> it.body.stream.copyTo(stream) }
                    downloaded[location] = LocalResource.DownloadedFile(file)
                }
            }
        }
    }

    private fun findResources(pdf: Pdf, locations: MutableSet<ResourceLocation>) = pdf.pages.forEach { page ->
        locations.addAll(page.stationary.map { it.template })
        page.blockContent.values.forEach { findResources(it, locations) }
        findResources(page.content, locations)
    }

    private fun ResourceLocation.withDependencies(locations: MutableSet<ResourceLocation>) {
        locations += this
        if (this is ResourceLocation.Remote.Custom) {
            arguments.filterIsInstance<Argument.ResourceArgument>().forEach {
                if (!locations.contains(it.value)) {
                    it.value.withDependencies(locations)
                }
            }
        }
    }

    private fun findResources(component: Component, locations: MutableSet<ResourceLocation>) {
        when(component) {
            is Component.Style -> (component.styleAttributes.font as? Font.Resource)?.resourceLocation?.withDependencies(locations)
            is Component.Image -> component.resource.withDependencies(locations)
            is Component.Cell.Composite -> findResources(component.content, locations)
            is Component.Cell.Image -> findResources(component.image, locations)
            is Component.Cell.Table -> findResources(component.table, locations)
            is Component.Cell.Text -> findResources(component.phrase, locations)
            is Component.ListItem -> findResources(component.phrase, locations)
            is Component.Content,
            is Component.Table,
            is Component.Anchor,
            is Component.Break.BlockBreak,
            is Component.Break.PageBreak,
            is Component.Chunk,
            is Component.ItemList,
            is Component.Link,
            is Component.PageNumber,
            is Component.Paragraph,
            is Component.Phrase -> Unit
        }.also {
            component.children.forEach { findResources(it, locations) }
        }
    }
}