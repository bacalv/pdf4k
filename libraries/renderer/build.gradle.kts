plugins {
    id("buildlogic.kotlin-library-conventions")
    id("buildlogic.kotlin-publishing-conventions")
}

dependencies {
    api(project(":libraries::domain"))
    implementation(libs.kotlinReflect)
    implementation(libs.openPdf)
    implementation(libs.bouncyCastleBcmail)
    testFixturesApi(project(":libraries::domain-dto"))
    testFixturesApi(project(":libraries::dsl"))
    testFixturesApi(project(":libraries::testing"))
    testFixturesApi(testFixtures(project(":libraries::domain")))
    testFixturesImplementation(libs.http4kCore)
    testFixturesImplementation(libs.http4kServerUndertow)
    testFixturesImplementation(libs.junitJupiterApi)
    testImplementation(testFixtures(project(":libraries::domain")))
    testImplementation(project(":libraries::domain-dto"))
    testImplementation(project(":libraries::dsl"))
    testImplementation(project(":libraries::testing"))
    testImplementation(platform(libs.http4kBom))
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

publishing {
    publications.named<MavenPublication>("mavenJava") {
        pom {
            name = "pdf4k Renderer"
            description = "Renders the pdf4k domain to a PDF file using iText / OpenPDF"
        }
    }
}