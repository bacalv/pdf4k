plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":libraries::domain"))
    implementation(libs.kotlinReflect)
    implementation(libs.openPdf)
    implementation(libs.qrCode)
    implementation(libs.bouncyCastleBcmail)
    testFixturesImplementation(project(":libraries::domain-dto"))
    testFixturesImplementation(project(":libraries::testing"))
    testFixturesImplementation(testFixtures(project(":libraries::domain")))
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