plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":domain"))
    implementation(libs.kotlinReflect)
    implementation(libs.openPdf)
    implementation(libs.qrCode)
    implementation(libs.bouncyCastleBcmail)
    testFixturesImplementation(project(":domain-dto"))
    testFixturesImplementation(project(":testing"))
    testFixturesImplementation(testFixtures(project(":domain")))
    testFixturesImplementation(libs.http4kCore)
    testFixturesImplementation(libs.http4kServerUndertow)
    testFixturesImplementation(libs.junitJupiterApi)
    testImplementation(testFixtures(project(":domain")))
    testImplementation(project(":domain-dto"))
    testImplementation(project(":dsl"))
    testImplementation(project(":testing"))
    testImplementation(platform(libs.http4kBom))
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}