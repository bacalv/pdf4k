package io.pdf4k.testing.domain

import java.net.ServerSocket

fun randomPort(): Int = ServerSocket(0).run {
    close()
    localPort
}
