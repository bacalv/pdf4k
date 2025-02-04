package io.pdf4k.server.service.realm

import io.pdf4k.server.domain.FileId
import io.pdf4k.server.domain.Realm
import io.pdf4k.server.domain.StationaryPack
import io.pdf4k.server.domain.StationaryPackList

class RealmService {
    data class StationaryPackKey(val realmName: String, var stationaryPackName: String)

    private val realms = mutableListOf<Realm>()
    private val realmStationaryPacks = mutableMapOf<Realm, MutableList<StationaryPack>>()
    private val stationaryPacks = mutableMapOf<StationaryPackKey, StationaryPack>()

    fun addRealm(realm: Realm) {
        realms += realm
    }

    fun listRealms(): List<Realm> = realms

    fun getRealm(realmName: String) = realms.firstOrNull { it.name == realmName }

    fun addStationaryPack(realmName: String, stationaryPackName: String) {
        val realm = getRealm(realmName)!!
        val stationaryPack = StationaryPack(stationaryPackName)
        realmStationaryPacks.getOrPut(realm) { mutableListOf() } += stationaryPack
        stationaryPacks[StationaryPackKey(realmName, stationaryPackName)] = stationaryPack
    }

    fun findStationaryPack(realmName: String, stationaryPackName: String): StationaryPack {
        return stationaryPacks[StationaryPackKey(realmName, stationaryPackName)]!!
    }

    fun listStationaryPacks(realmName: String): StationaryPackList {
        val stationaryPackNames = realmStationaryPacks.getOrDefault(getRealm(realmName), emptyList()).map { it.name }
        return StationaryPackList(stationaryPackNames);
    }

    fun uploadPageTemplate(realmName: String, stationaryPackName: String, fileId: FileId, name: String) {
        val stationaryPack = findStationaryPack(realmName, stationaryPackName)
        stationaryPack.pageTemplates += name to fileId
    }

    fun uploadFont(realmName: String, stationaryPackName: String, fileId: FileId, name: String) {
        val stationaryPack = findStationaryPack(realmName, stationaryPackName)
        stationaryPack.fonts += name to fileId
    }

    fun uploadImage(realmName: String, stationaryPackName: String, fileId: FileId, name: String) {
        val stationaryPack = findStationaryPack(realmName, stationaryPackName)
        stationaryPack.images += name to fileId
    }
}