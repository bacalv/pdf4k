plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":plugins:thumbnails::domain"))
    api(project(":libraries::domain-dto"))
    api(project(":libraries::dsl"))
    testImplementation(libs.mockk)
}

tasks.jar {
    archiveFileName.set("thumbnails-${project.name}-${project.version}.jar")
}