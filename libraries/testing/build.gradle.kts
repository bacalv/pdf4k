plugins {
    id("buildlogic.kotlin-library-conventions")
    id("buildlogic.kotlin-publishing-conventions")
}

dependencies {
    api(libs.junitJupiterApi)
    api(libs.junitJupiterParams)
    api(libs.okeyDoke)
    implementation(libs.pdfBox)
    testImplementation(libs.openPdf)
}

publishing {
    publications.named<MavenPublication>("mavenJava") {
        pom {
            name = "pdf4k Approval Testing Library"
            description = "A JUnit5 extension based on okeydoke that allows you to create PDF approval tests"
        }
    }
}