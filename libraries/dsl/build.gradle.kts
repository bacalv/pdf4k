plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":libraries::domain"))
    testImplementation(libs.mockk)
}