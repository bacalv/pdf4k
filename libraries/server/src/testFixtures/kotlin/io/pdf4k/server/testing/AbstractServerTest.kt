package io.pdf4k.server.testing

import io.pdf4k.client.Pdf4kServerClient
import io.pdf4k.server.scenario.Operator
import io.pdf4k.server.scenario.Scenario
import io.pdf4k.testing.PdfApprover
import io.pdf4k.testing.PdfApproverExtension
import org.http4k.client.OkHttp
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(Pdf4kServerTestExtension::class, PdfApproverExtension::class)
abstract class AbstractServerTest {
    private val instance = Pdf4kServerTestExtension.instance()
    fun emptyScenario(): Scenario {
        val handler = SetBaseUriFrom(Uri.of("http://localhost:${instance.port}")).then(OkHttp())
        val client = Pdf4kServerClient(handler)
        val operator = Operator(client)
        return Scenario(handler, operator)
    }

    fun ByteArray.approve(approver: PdfApprover) {
        approver.assertApproved(this)
    }
}