plugins {
    id("buildlogic.kotlin-library-conventions")
    id("buildlogic.kotlin-publishing-conventions")
}

dependencies {
    api(project(":libraries::domain"))
    implementation(libs.jacksonAnnotations)
    implementation(libs.jacksonCore)
    implementation(libs.jacksonDatatypeJsr310)
    implementation(libs.jacksonModuleKotlin)
    testImplementation(testFixtures(project(":libraries::domain")))
    testImplementation(project(":libraries::testing"))
    testImplementation(project(":libraries::dsl"))
    testImplementation(kotlin("test"))
    testFixturesImplementation(libs.okeyDoke)
    testFixturesApi(testFixtures(project(":libraries::domain")))
    testFixturesApi(project(":libraries::testing"))
    testFixturesApi(project(":libraries::dsl"))
}

publishing {
    publications.named<MavenPublication>("mavenJava") {
        pom {
            name = "pdf4k Domain Data Transfer Objects"
            description = "Objects to use for serializing and deserializing the pdf4k domain to and from JSON"
        }
    }
}