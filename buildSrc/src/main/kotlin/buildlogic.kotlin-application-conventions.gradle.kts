plugins {
    id("buildlogic.kotlin-common-conventions")
    id("com.bmuschko.docker-remote-api")
    id("com.bmuschko.docker-java-application")
    application
}

repositories {
    gradlePluginPortal()
}

docker {
    javaApplication {
        baseImage.set("openjdk:21-ea-1-slim")
        maintainer.set("Bret Calvey 'bacalv@gmail.com'")
        ports.set(listOf(8080))
        jvmArgs.set(listOf("-Xms256m", "-Xmx2048m"))
    }
}