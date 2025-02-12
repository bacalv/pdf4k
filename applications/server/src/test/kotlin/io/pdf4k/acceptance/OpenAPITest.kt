package io.pdf4k.acceptance

import com.oneeyedmen.okeydoke.Approver
import io.pdf4k.json.testing.jsonApprover
import io.pdf4k.testing.AbstractServerTest
import io.pdf4k.testing.prettifyJson
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class OpenAPITest: AbstractServerTest() {
    @RegisterExtension
    val approvalExtension = jsonApprover()

    @Test
    fun `renders open API documentation`(approver: Approver): Unit = with(emptyScenario()) {
        val body = operator.viewsOpenAPIDocs()
        approver.assertApproved(prettifyJson(body))
    }
}