package io.pdf4k.extensions

fun String.singleLine() = trimIndent().replace("\n", " ")

fun String.splitParagraphs() = trimIndent().split("\n\n").map { paragraph ->
    paragraph.trim().split("\n").joinToString(" ") { line -> line.trim() } + "\n\n"
}
