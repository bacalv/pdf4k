plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":libraries::server"))
    implementation(testFixtures(project(":libraries::domain")))
    testImplementation(testFixtures(project(":libraries::server")))
}

val pdfServerConfigLocation = "/musicians-pdf4k-config.yaml"

docker {
    javaApplication {
        mainClassName = "io.pdf4k.server.Pdf4kServerMain"
        jvmArgs.set(listOf(
            "-Xms256m",
            "-Xmx2048m",
            "-DPDF4K_CONFIG_LOCATION=$pdfServerConfigLocation"
        ))
    }
}

tasks.withType<Test> {
    systemProperty("PDF4K_CONFIG_LOCATION", pdfServerConfigLocation)
}