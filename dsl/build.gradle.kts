plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":domain"))
    testImplementation(libs.mockk)
}