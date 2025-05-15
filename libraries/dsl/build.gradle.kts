plugins {
    id("buildlogic.kotlin-library-conventions")
    id("buildlogic.kotlin-publishing-conventions")
}

dependencies {
    api(project(":libraries::domain"))
    testImplementation(libs.mockk)
}

publishing {
    publications.named<MavenPublication>("mavenJava") {
        pom {
            name = "PDF4K DSL"
            description = "Kotlin domain-specific language for creating PDF files"
        }
    }
}