package io.pdf4k.server.config

data class Pdf4kServerConfiguration(val port: Int) {
    companion object {
        fun loadConfig() = Pdf4kServerConfiguration(8080)
    }
}