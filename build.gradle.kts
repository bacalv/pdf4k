plugins {
    kotlin("jvm") version "1.9.24"
    application
    `java-test-fixtures`
    idea
}

group = "pro.juxt.pdf4k"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.librepdf:openpdf:2.0.3")
    implementation("io.github.g0dkar:qrcode-kotlin:4.2.0")
//    implementation("org.bouncycastle:bcprov-jdk18on:1.80")
    implementation("org.bouncycastle:bcmail-jdk18on:1.80")

    testImplementation(kotlin("test"))
    testFixturesImplementation(kotlin("test"))
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-params:5.11.3")
    testFixturesImplementation("com.oneeyedmen:okeydoke:2.0.3")
    testFixturesImplementation("org.apache.pdfbox:pdfbox:3.0.3")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}