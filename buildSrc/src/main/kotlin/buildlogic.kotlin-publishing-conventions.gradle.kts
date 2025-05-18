plugins {
    java
    `maven-publish`
    id("org.jreleaser")
}

group = "io.pdf4k"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                url = "https://github.com/bacalv/pdf4k"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit/"
                    }
                }
                developers {
                    developer {
                        id = "bacalv"
                        name = "Bret Adam Calvey"
                        email = "bacalv@gmail.com"
                        url = "https://github.com/bacalv"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/bacalv/pdf4k.git"
                    developerConnection = "scm:git:ssh://github.com:bacalv/pdf4k.git"
                    url = "https://github.com/bacalv/pdf4k/tree/main"
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

val signingPublicKey: String? by project
val signingSecretKey: String? by project
val signingPassphrase: String? by project
val sonatypeUsername: String? by project
val sonatypePassword: String? by project

jreleaser {
    gitRootSearch = true
    strict = false
    signing {
        passphrase = signingPassphrase
        publicKey = signingPublicKey
        secretKey = signingSecretKey
        setActive("ALWAYS")
        armored = true
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    username = sonatypeUsername
                    password = sonatypePassword
                    setActive("ALWAYS")
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
    release {
        github {
            token = "blank"
        }
    }
}