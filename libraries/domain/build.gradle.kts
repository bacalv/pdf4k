plugins {
    id("buildlogic.kotlin-library-conventions")
    id("buildlogic.kotlin-publishing-conventions")
}

dependencies {
    testFixturesImplementation(libs.junitJupiterApi)
    testFixturesImplementation(libs.junitJupiterParams)
}

publishing {
    publications.named<MavenPublication>("mavenJava") {
        pom {
            name = "pdf4k Domain"
            description = "Domain library for pdf4k"
        }
    }
}