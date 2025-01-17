package io.pdf4k.testing

import com.lowagie.text.pdf.*

fun main() {
    val pdfPath = "/Users/bac/IdeaProjects/pdf4k/src/test/resources/io/pdf4k/renderer/LinksTest.adds a link from a block to main content.actual.pdf"  // Update with the path to your PDF
    val reader = PdfReader(pdfPath)

    reader.namedDestination
    // Iterate through all pages (we need to get all dictionaries)
    for (i in 1..reader.numberOfPages) {
        val pageDictionary = reader.getPageN(i)
        traverseDictionary(reader, pageDictionary, "Page $i Dictionary")
    }
}

fun traverseDictionary(reader: PdfReader, pdfDictionary: PdfDictionary, context: String) {
    println("Traversing dictionary in context: $context")
    val keys = pdfDictionary.keys.iterator()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = pdfDictionary.get(key)

        if (value is PRIndirectReference) {
            val obj = reader.getPdfObject(value.number)
            println(obj)
        }
        // Print key and the type of the object
        val valueType = when (value) {
            is PdfDictionary -> "PdfDictionary"
            is PdfArray -> "PdfArray"
            else -> value.javaClass.simpleName
        }

        println("Key: $key, Type: $valueType, Reference: ${value}")

        // Recursively traverse if the value is a dictionary or array
        when (value) {
            is PdfDictionary -> traverseDictionary(reader, value, "Dictionary under key: $key")
            is PdfArray -> traverseArray(reader, value, "Array under key: $key")
        }
    }
}

fun traverseArray(reader: PdfReader, pdfArray: PdfArray, context: String) {
    println("Traversing array in context: $context")
    for (i in 0 until pdfArray.size()) {
        val element = pdfArray.elements.get(i)
        val elementType = when (element) {
            is PdfDictionary -> "PdfDictionary"
            is PdfArray -> "PdfArray"
            else -> element.javaClass.simpleName
        }
        println("Index: $i, Type: $elementType, Reference: ${element}, Object: ${element}")

        // Recursively traverse if the element is a dictionary or array
        when (element) {
            is PdfDictionary -> traverseDictionary(reader, element, "Dictionary in array at index $i")
            is PdfArray -> traverseArray(reader, element, "Array in array at index $i")
        }
    }
}