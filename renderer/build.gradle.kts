plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(project(":domain"))
    implementation(libs.kotlinReflect)
    implementation(libs.openPdf)
    implementation(libs.qrCode)
    implementation(libs.bouncyCastleBcmail)
    testImplementation(project(":dsl"))
    testImplementation(project(":testing"))
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}