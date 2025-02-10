package io.pdf4k.scenario

import io.pdf4k.testing.domain.RandomUtils.randomString

data class Scenario(
    val operator: Operator,
    val realmName: String = randomString(),
    val stationaryPackName: String = randomString()
)