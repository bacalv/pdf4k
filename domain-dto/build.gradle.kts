plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":domain"))
    implementation(libs.jacksonAnnotations)
    implementation(libs.jacksonCore)
    implementation(libs.jacksonDatatypeJsr310)
    implementation(libs.jacksonModuleKotlin)
    testImplementation(testFixtures(project(":domain")))
    testImplementation(project(":testing"))
    testImplementation(project(":dsl"))
    testImplementation(kotlin("test"))
}