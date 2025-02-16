package io.pdf4k.server.scenario

import org.http4k.core.HttpHandler

data class Scenario(
    val handler: HttpHandler,
    val operator: Operator
)