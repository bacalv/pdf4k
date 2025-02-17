package io.pdf4k.provider

import io.pdf4k.domain.Argument
import java.io.InputStream

interface CustomResourceProvider {
    val name: String
    fun load(arguments: List<Argument>, resourceLocator: ResourceLocator): InputStream?
}