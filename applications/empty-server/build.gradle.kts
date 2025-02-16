plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":libraries:server"))
    testImplementation(testFixtures(project(":libraries::server")))
}

docker {
    javaApplication {
        mainClassName = "io.pdf4k.server.Pdf4kServerMain"
        jvmArgs.set(
            listOf(
                "-Xms256m",
                "-Xmx2048m"
            )
        )
    }
}
