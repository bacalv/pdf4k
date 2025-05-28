plugins {
    id("buildlogic.kotlin-common-conventions")

    `java-library`
    `java-test-fixtures`
    jacoco
}

tasks.jacocoTestReport {
    enabled = false
}