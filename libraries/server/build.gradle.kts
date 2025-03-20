plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(libs.http4kCore)
    api(libs.http4kFormatJackson)
    api(project(":libraries:domain"))
    api(project(":libraries::domain-dto"))
    api(project(":libraries::dsl"))
    api(project(":libraries::renderer"))
    implementation(platform(libs.http4kBom))
    implementation(libs.http4kApiOpenApi)
    implementation(libs.http4kContract)
    implementation(libs.http4kMultipart)
    implementation(libs.http4kServerUndertow)
    implementation(libs.http4kClientOkHttp)
    implementation(libs.jacksonDataFormatYaml)
    implementation(libs.jacksonDatatypeJsr310)
    testFixturesApi(project(":libraries::testing"))
    testFixturesApi(project(":libraries::dsl"))
    testFixturesApi(project(":libraries::client"))
    testFixturesApi(testFixtures(project(":libraries::domain")))
    testFixturesApi(testFixtures(project(":libraries::domain-dto")))
    testFixturesApi(testFixtures(project(":libraries::renderer")))
    testFixturesApi(libs.http4kClientOkHttp)
    testFixturesImplementation(libs.http4kServerUndertow)
    testFixturesImplementation(libs.awaitilityKotlin)
}
