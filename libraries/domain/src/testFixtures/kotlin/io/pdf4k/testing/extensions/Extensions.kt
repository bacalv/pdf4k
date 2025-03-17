package io.pdf4k.testing.extensions

import org.junit.jupiter.params.provider.Arguments

fun String.singleLine() = trimIndent().replace("\n", " ")

fun String.splitParagraphs() = trimIndent().split("\n\n").map { paragraph ->
    paragraph.trim().split("\n").joinToString(" ") { line -> line.trim() }
}

fun List<*>.cartesianProduct(bList: List<*>) = map { a ->
    bList.map { b ->
        Arguments.arguments(a, b)
    }
}.flatten()
