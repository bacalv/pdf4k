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
include(
    "libraries:testing",
    "libraries:domain",
    "libraries:domain-dto",
    "libraries:dsl",
    "libraries:renderer",
    "libraries:client",
    "libraries:server",
    "plugins:markdown",
    "applications:empty-server",
    "applications:examples:musician-renderer"
)
