plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":domain"))
    api(project(":domain-dto"))
    api(project(":dsl"))
    implementation(platform(libs.http4kBom))
    implementation(libs.http4kCore)
    implementation(libs.http4kFormatJackson)
    implementation(libs.http4kMultipart)
    implementation(libs.jacksonDatatypeJsr310)
    testImplementation(testFixtures(project(":domain")))
    testImplementation(project(":testing"))
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}