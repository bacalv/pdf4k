package io.pdf4k.server.testing

import io.pdf4k.domain.KeyName
import io.pdf4k.testing.PdfApprover
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.ZonedDateTime

class ServerApplicationTest : AbstractServerTest() {
    @Test
    fun `renders a pdf signed with test key`(approver: PdfApprover) = with(emptyScenario()) {
        operator.rendersAPdfImmediately {
            sign(
                keyName = KeyName("test-key"),
                reason = "Test Reason",
                location = "Test Location",
                contact = "Test Contact",
                signDate = ZonedDateTime.now(Clock.systemDefaultZone())
            )
            page {
                content {
                    +"This is signed using the test-key"
                }
            }
        }.approve(approver)
    }
}