plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":libraries::domain"))
    api(project(":libraries::domain-dto"))
    api(project(":libraries::dsl"))
    implementation(platform(libs.http4kBom))
    implementation(libs.http4kCore)
    implementation(libs.http4kFormatJackson)
    implementation(libs.http4kMultipart)
    implementation(libs.jacksonDatatypeJsr310)
    testImplementation(testFixtures(project(":libraries::domain")))
    testImplementation(project(":libraries::testing"))
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}