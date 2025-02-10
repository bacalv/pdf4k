package io.pdf4k.server.endpoints

import io.pdf4k.server.domain.FileId
import io.pdf4k.server.domain.ServerLens.fileFormBodyLens
import io.pdf4k.server.domain.ServerLens.realmPathLens
import io.pdf4k.server.domain.ServerLens.stationaryPackLens
import io.pdf4k.server.domain.ServerLens.stationaryPackListLens
import io.pdf4k.server.domain.ServerLens.stationaryPackPathLens
import io.pdf4k.server.endpoints.response.StationaryPackListResponse
import io.pdf4k.server.endpoints.response.StationaryPackResponse
import io.pdf4k.server.service.Pdf4kServices
import org.http4k.contract.PreFlightExtraction
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method.*
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ProcessFiles
import org.http4k.filter.ServerFilters

object StationaryPackRoutes {
    fun routes(services: Pdf4kServices) = listOf(
        "/realms" / realmPathLens / stationaryPackPathLens meta {
            summary = "Creates a stationary pack."
        } bindContract PUT to { realmName, stationaryPackName ->
            {
                services.realmService.addStationaryPack(realmName, stationaryPackName)
                Response(OK)
            }
        },

        "/realms" / realmPathLens / stationaryPackPathLens meta {
            summary = "Find a stationary pack."
        } bindContract GET to { realmName, stationaryPackName ->
            {
                services.realmService.findStationaryPack(realmName, stationaryPackName).let {
                    val response = StationaryPackResponse(
                        name = it.name,
                        pageTemplates = it.pageTemplates.keys.toList(),
                        fonts = it.fonts.keys.toList(),
                        images = it.images.keys.toList()
                    )
                    Response(OK).with(stationaryPackLens of response)
                }
            }
        },

        "/realms" / realmPathLens meta {
            summary = "List stationary packs."
        } bindContract GET to { realmName ->
            {
                val list = services.realmService.listStationaryPacks(realmName)
                Response(OK).with(stationaryPackListLens of StationaryPackListResponse(list.stationaryPacks))
            }
        },

        "/realms" / realmPathLens / stationaryPackPathLens / "page-template" meta {
            summary = "Upload a page template."
            preFlightExtraction = PreFlightExtraction.IgnoreBody
        } bindContract POST to { realmName, stationaryPackName, _ ->
            ServerFilters.ProcessFiles { file ->
                services.multipartFileStore.store(file)
            }.then { request ->
                val body = fileFormBodyLens(request)
                val fileId = FileId(body.fields["file"]?.first() ?: TODO())
                val fileName = body.fields["name"]?.first() ?: TODO()
                services.realmService.uploadPageTemplate(realmName, stationaryPackName, fileId, fileName)
                Response(OK)
            }
        },

        "/realms" / realmPathLens / stationaryPackPathLens / "font" meta {
            summary = "Upload a font."
            preFlightExtraction = PreFlightExtraction.IgnoreBody
        } bindContract POST to { realmName, stationaryPackName, _ ->
            ServerFilters.ProcessFiles { file ->
                services.multipartFileStore.store(file)
            }.then { request ->
                val body = fileFormBodyLens(request)
                val fileId = FileId(body.fields["file"]?.first() ?: TODO())
                val fileName = body.fields["name"]?.first() ?: TODO()
                services.realmService.uploadFont(realmName, stationaryPackName, fileId, fileName)
                Response(OK)
            }
        },

        "/realms" / realmPathLens / stationaryPackPathLens / "image" meta {
            summary = "Upload an image."
            preFlightExtraction = PreFlightExtraction.IgnoreBody
        } bindContract POST to { realmName, stationaryPackName, _ ->
            ServerFilters.ProcessFiles { file ->
                services.multipartFileStore.store(file)
            }.then { request ->
                val body = fileFormBodyLens(request)
                val fileId = FileId(body.fields["file"]?.first() ?: TODO())
                val fileName = body.fields["name"]?.first() ?: TODO()
                services.realmService.uploadImage(realmName, stationaryPackName, fileId, fileName)
                Response(OK)
            }
        }
    )
}