package io.pdf4k.server.testing

import io.pdf4k.server.config.Pdf4kServerConfiguration
import io.pdf4k.server.service.Pdf4kServerInstance
import io.pdf4k.testing.domain.randomPort
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.concurrent.atomic.AtomicReference

class Pdf4kServerTestExtension: BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    companion object {
        private val instance = AtomicReference<Pdf4kServerInstance>()
        fun instance() = instance.get() ?: throw IllegalStateException("Server not started")
    }

    override fun beforeAll(context: ExtensionContext?) {
        if (instance.get() == null) {
            val server = Pdf4kServerConfiguration.Companion.loadConfig().copy(port = randomPort()).pdf4kServer()
            instance.set(server)
            server.start()
        }
    }

    override fun close() {
        instance.get()?.stop()
    }
}