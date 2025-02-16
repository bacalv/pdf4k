package io.pdf4k.server.testing

import com.fasterxml.jackson.databind.ObjectMapper

fun prettifyJson(json: String): String = ObjectMapper().reader().readTree(json).toPrettyString()
