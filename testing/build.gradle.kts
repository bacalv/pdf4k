plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api(libs.junitJupiterApi)
    api(libs.junitJupiterParams)
    api(libs.okeyDoke)
    implementation(libs.pdfBox)
    testImplementation(libs.openPdf)
}
