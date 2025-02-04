package io.pdf4k.testing

import io.pdf4k.server.config.Pdf4kServerConfiguration
import io.pdf4k.server.pdf4kServer
import io.pdf4k.server.service.Pdf4kServerInstance
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.concurrent.atomic.AtomicReference

class Pdf4kServerTestExtension: BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    companion object {
        private val instance = AtomicReference<Pdf4kServerInstance>()
        private val testConfiguration = Pdf4kServerConfiguration(8080)//randomPort())

        fun instance() = instance.get() ?: throw IllegalStateException("Server not started")
    }

    override fun beforeAll(context: ExtensionContext?) {
        if (instance.get() == null) {
            val server = testConfiguration.pdf4kServer()
            instance.set(server)
            server.start()
        }
    }

    override fun close() {
        instance.get()?.stop()
    }
}