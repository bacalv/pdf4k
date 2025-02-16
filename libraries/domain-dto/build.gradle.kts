plugins {
    id("buildlogic.kotlin-library-conventions")
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