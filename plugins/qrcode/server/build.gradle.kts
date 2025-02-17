plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":plugins::qrcode::dsl"))
    api(project(":libraries::server"))
    implementation(libs.qrCode)
    testImplementation(testFixtures(project(":libraries::server")))
    testImplementation(libs.mockk)
}

val pdfServerConfigLocation = "/test-qrcode-pdf4k-config.yaml"

tasks.withType<Test> {
    systemProperty("PDF4K_CONFIG_LOCATION", pdfServerConfigLocation)
}

tasks.jar {
    archiveFileName.set("qrcode-${project.name}-${project.version}.jar")
}