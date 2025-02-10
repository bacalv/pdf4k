plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    implementation(project(":libraries:dsl"))
    implementation(libs.commonMark)
    testImplementation(project(":libraries:testing"))
    testImplementation(testFixtures(project(":libraries:renderer")))
    testImplementation(project(":libraries:renderer"))
}