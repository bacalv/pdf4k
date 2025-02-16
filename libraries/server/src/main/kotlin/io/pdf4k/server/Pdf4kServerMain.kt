package io.pdf4k.server

import io.pdf4k.server.config.Pdf4kServerConfiguration.Companion.loadConfig

object Pdf4kServerMain {
    @JvmStatic
    fun main(args: Array<String>) {
        loadConfig().pdf4kServer().start()
    }
}