plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    testFixturesImplementation(libs.junitJupiterApi)
    testFixturesImplementation(libs.junitJupiterParams)
}