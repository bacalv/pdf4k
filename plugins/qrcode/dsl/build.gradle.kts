plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":plugins::qrcode::domain"))
    api(project(":libraries::domain-dto"))
    api(project(":libraries::dsl"))
    testImplementation(libs.mockk)
}

tasks.jar {
    archiveFileName.set("qrcode-${project.name}-${project.version}.jar")
}