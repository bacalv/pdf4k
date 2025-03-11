plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":plugins::thumbnails::dsl"))
    api(project(":libraries::server"))
    implementation(libs.thumbnailator)
    testImplementation(testFixtures(project(":libraries::server")))
    testImplementation(libs.mockk)
}

val pdfServerConfigLocation = "/test-thumbnails-pdf4k-config.yaml"

tasks.withType<Test> {
    systemProperty("PDF4K_CONFIG_LOCATION", pdfServerConfigLocation)
}

tasks.jar {
    archiveFileName.set("thumbnails-${project.name}-${project.version}.jar")
}