plugins {
    java
    `maven-publish`
    signing
}

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
            name = "ossrh"
            url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            credentials {
                val ossrhUsername: String? by project
                val ossrhToken: String? by project
                username = ossrhUsername
                password = ossrhToken
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}