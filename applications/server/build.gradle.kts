plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":libraries:domain"))
    implementation(project(":libraries::domain-dto"))
    implementation(project(":libraries::renderer"))
    implementation(platform(libs.http4kBom))
    implementation(libs.http4kApiOpenApi)
    implementation(libs.http4kCore)
    implementation(libs.http4kContract)
    implementation(libs.http4kFormatJackson)
    implementation(libs.http4kMultipart)
    implementation(libs.http4kServerUndertow)
    implementation(libs.jacksonDatatypeJsr310)
    testImplementation(project(":libraries::testing"))
    testImplementation(project(":libraries::dsl"))
    testImplementation(project(":libraries::client"))
    testImplementation(testFixtures(project(":libraries::domain")))
    testImplementation(testFixtures(project(":libraries::domain-dto")))
    testImplementation(libs.http4kClientOkHttp)
}

application {
    mainClass.set("io.pdf4k.server.Pdf4kServerMain")
}
