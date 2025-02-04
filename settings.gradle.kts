pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "pdf4k"
include("testing", "domain", "domain-dto", "dsl", "renderer", "client", "server")
