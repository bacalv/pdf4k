package io.pdf4k.scenario

import io.pdf4k.client.Pdf4kServerClient
import io.pdf4k.client.domain.ClientLens.realmListLens
import io.pdf4k.client.domain.ClientLens.stationaryPackLens
import io.pdf4k.client.domain.ClientLens.stationaryPacksListLens
import io.pdf4k.client.domain.RealmList
import io.pdf4k.client.domain.StationaryPack
import io.pdf4k.client.domain.StationaryPackList
import io.pdf4k.domain.ResourceLocation.Companion.classpathResource
import io.pdf4k.dsl.PdfBuilder
import io.pdf4k.server.service.realm.RealmService.Companion.DEFAULT_REALM_NAME
import io.pdf4k.server.service.realm.RealmService.Companion.DEFAULT_STATIONARY_PACK_NAME
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.ByteArrayOutputStream

class Operator(private val client: Pdf4kServerClient) {
    fun createsRealm(realmName: String, expectedStatus: Status = OK) {
        val response = client.createRealm(realmName)
        assertEquals(expectedStatus, response.status)
    }

    fun listsRealms(expectedStatus: Status = OK): RealmList {
        val response = client.listRealms()
        assertEquals(expectedStatus, response.status)
        return if (expectedStatus == OK) realmListLens(response) else RealmList(emptyList())
    }

    fun createsStationaryPack(realmName: String, stationaryPackName: String, expectedStatus: Status = OK) {
        val response = client.createStationaryPack(realmName, stationaryPackName)
        assertEquals(expectedStatus, response.status)
    }

    fun listsStationaryPacksForRealm(realmName: String, expectedStatus: Status = OK): StationaryPackList {
        val response = client.listsStationaryPacksForRealm(realmName)
        assertEquals(expectedStatus, response.status)
        return if (expectedStatus == OK) stationaryPacksListLens(response) else StationaryPackList(emptyList())
    }

    fun findsStationaryPack(realmName: String, stationaryPackName: String, expectedStatus: Status = OK): StationaryPack? {
        val response = client.findStationaryPack(realmName, stationaryPackName)
        assertEquals(expectedStatus, response.status)
        return if (expectedStatus == OK) stationaryPackLens(response) else null
    }

    fun uploadsPageTemplate(
        realmName: String,
        stationaryPackName: String,
        localPdfFile: String,
        pageTemplateName: String,
        expectedStatus: Status = OK
    ) {
        val inputStream = classpathResource("/stationary/$localPdfFile")
        val response = client.uploadPageTemplate(realmName, stationaryPackName, inputStream, pageTemplateName)
        assertEquals(expectedStatus, response.status)
    }

    fun uploadsFont(
        realmName: String,
        stationaryPackName: String,
        localFontFile: String,
        fontName: String,
        expectedStatus: Status = OK
    ) {
        val inputStream = classpathResource("/fonts/$localFontFile")
        val response = client.uploadFont(realmName, stationaryPackName, inputStream, fontName)
        assertEquals(expectedStatus, response.status)
    }

    fun uploadsImage(
        realmName: String,
        stationaryPackName: String,
        localImageFile: String,
        imageName: String,
        expectedStatus: Status = OK
    ) {
        val inputStream = classpathResource("/images/$localImageFile")
        val response = client.uploadImage(realmName, stationaryPackName, inputStream, imageName)
        assertEquals(expectedStatus, response.status)
    }

    fun rendersAPdfImmediately(
        realmName: String = DEFAULT_REALM_NAME,
        stationaryPackName: String = DEFAULT_STATIONARY_PACK_NAME,
        expectedStatus: Status = OK,
        block: PdfBuilder.() -> Unit
    ): ByteArray {
        val response = client.renderImmediate(realmName, stationaryPackName, block)
        assertEquals(expectedStatus, response.status)
        val result = ByteArrayOutputStream()
        response.body.stream.copyTo(result)
        return result.toByteArray()
    }
}