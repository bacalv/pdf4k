package io.pdf4k.acceptance

import io.pdf4k.testing.AbstractServerTest
import io.pdf4k.testing.domain.RandomUtils.randomString
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RealmTest : AbstractServerTest() {
    @Test
    fun `create new realm`(): Unit = with(emptyScenario()) {
        val realmName = randomString()
        operator.createsRealm(realmName)
        assertTrue(operator.listsRealms().realms.contains(realmName))
    }
}