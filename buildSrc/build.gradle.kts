plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.bmuschko:gradle-docker-plugin:9.4.0")
    implementation("org.jreleaser:org.jreleaser.gradle.plugin:1.18.0")
    implementation(libs.kotlin.gradle.plugin)
}
