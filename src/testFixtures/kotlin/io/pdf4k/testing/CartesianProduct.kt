package io.pdf4k.testing

import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments

object CartesianProduct {
    fun cartesianProduct(aList: List<*>, bList: List<*>): List<Arguments> {
        return aList.map { a ->
            bList.map { b ->
                arguments(a, b)
            }
        }.flatten()
    }
}