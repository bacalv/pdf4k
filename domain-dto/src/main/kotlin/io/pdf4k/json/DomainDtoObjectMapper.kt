package io.pdf4k.json

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

val domainDtoObjectMapper: ObjectMapper = ObjectMapper()
    .registerModule(KotlinModule.Builder().build())
    .registerModule(JavaTimeModule())
    .setSerializationInclusion(NON_NULL)

fun Any.toPrettyJsonString(): String = domainDtoObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

inline fun <reified T : Any> String.toObject() : T = domainDtoObjectMapper.readValue(this, T::class.java)