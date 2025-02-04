package io.pdf4k.server.endpoints

import io.pdf4k.server.domain.Realm
import io.pdf4k.server.domain.ServerLens.realmListLens
import io.pdf4k.server.domain.ServerLens.realmPathLens
import io.pdf4k.server.endpoints.response.RealmListResponse
import io.pdf4k.server.service.Pdf4kServices
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with

object RealmRoutes {
    fun routes(services: Pdf4kServices) = listOf(
        "/" / realmPathLens meta {
            summary = "Creates a new realm."
            returning(OK)
        } bindContract PUT to { realmName ->
            {
                val realm = Realm(realmName)
                services.realmService.addRealm(realm)
                Response(OK)
            }
        },

        "/" meta {
            summary = "List realms."
            returning(OK, realmListLens to exampleRealmList)
        } bindContract GET to { _: Request ->
            val result = RealmListResponse(services.realmService.listRealms().map { it.name })
            Response(OK).with(realmListLens of result)
        }
    )

    private val exampleRealmList = RealmListResponse(listOf("realm-1", "realm-2"))
}