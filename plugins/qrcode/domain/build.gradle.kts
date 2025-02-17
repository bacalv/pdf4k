plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    implementation(project(":libraries:domain"))
    testImplementation(libs.mockk)
}

tasks.jar {
    archiveFileName.set("qrcode-${project.name}-${project.version}.jar")
}