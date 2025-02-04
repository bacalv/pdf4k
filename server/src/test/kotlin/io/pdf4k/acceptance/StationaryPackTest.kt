package io.pdf4k.acceptance

import io.pdf4k.testing.AbstractServerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StationaryPackTest : AbstractServerTest() {
    @Test
    fun `create new stationary pack`(): Unit = with(emptyScenario()) {
        operator.createsRealm(realmName)
        operator.createsStationaryPack(realmName, stationaryPackName)
        assertTrue(operator.listsStationaryPacksForRealm(realmName).stationaryPacks.contains(stationaryPackName))
    }

    @Test
    fun `upload page template to stationary pack`(): Unit = with(emptyScenario()) {
        operator.createsRealm(realmName)
        operator.createsStationaryPack(realmName, stationaryPackName)
        operator.uploadsPageTemplate(realmName, stationaryPackName, "example.pdf", "Example")
        operator.findsStationaryPack(realmName, stationaryPackName)?.pageTemplates?.let { list ->
            assertEquals(listOf("Example"), list)
        }
    }

    @Test
    fun `upload font to stationary pack`(): Unit = with(emptyScenario()) {
        operator.createsRealm(realmName)
        operator.createsStationaryPack(realmName, stationaryPackName)
        operator.uploadsFont(realmName, stationaryPackName, "CookieCrisp-L36ly.ttf", "CookieCrisp")
        operator.findsStationaryPack(realmName, stationaryPackName)?.fonts?.let { list ->
            assertEquals(listOf("CookieCrisp"), list)
        }
    }

    @Test
    fun `upload image to stationary pack`(): Unit = with(emptyScenario()) {
        operator.createsRealm(realmName)
        operator.createsStationaryPack(realmName, stationaryPackName)
        operator.uploadsImage(realmName, stationaryPackName, "musicians/hendrix.png", "Hendrix")
        operator.findsStationaryPack(realmName, stationaryPackName)?.images?.let { list ->
            assertEquals(listOf("Hendrix"), list)
        }
    }
}