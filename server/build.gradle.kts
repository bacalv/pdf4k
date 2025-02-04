plugins {
    id("buildlogic.kotlin-application-conventions")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":domain-dto"))
    implementation(project(":renderer"))
    implementation(platform(libs.http4kBom))
    implementation(libs.http4kApiOpenApi)
    implementation(libs.http4kCore)
    implementation(libs.http4kContract)
    implementation(libs.http4kFormatJackson)
    implementation(libs.http4kMultipart)
    implementation(libs.http4kServerUndertow)
    implementation(libs.jacksonDatatypeJsr310)
    testImplementation(project(":testing"))
    testImplementation(project(":dsl"))
    testImplementation(project(":client"))
    testImplementation(testFixtures(project(":domain")))
    testImplementation(libs.http4kClientOkHttp)
}