package io.pdf4k.provider

import java.io.InputStream

interface CustomResourceProvider {
    fun load(name: String): InputStream?
}