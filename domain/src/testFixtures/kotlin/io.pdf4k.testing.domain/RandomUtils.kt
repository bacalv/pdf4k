package io.pdf4k.testing.domain

object RandomUtils {
    fun randomString(len: Int = 10): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..len)
            .map { allowedChars.random() }
            .joinToString("")
    }
}