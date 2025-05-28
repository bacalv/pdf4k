plugins {
    base
    id("jacoco-report-aggregation")
}

repositories {
    mavenCentral()
}

dependencies {
    jacocoAggregation(project(":libraries:testing"))
    jacocoAggregation(project(":libraries:domain"))
    jacocoAggregation(project(":libraries:domain-dto"))
    jacocoAggregation(project(":libraries:dsl"))
    jacocoAggregation(project(":libraries:renderer"))
    jacocoAggregation(project(":libraries:client"))
    jacocoAggregation(project(":libraries:server"))
    jacocoAggregation(project(":plugins:markdown"))
    jacocoAggregation(project(":plugins:qrcode:domain"))
    jacocoAggregation(project(":plugins:qrcode:dsl"))
    jacocoAggregation(project(":plugins:qrcode:server"))
    jacocoAggregation(project(":plugins:thumbnails:domain"))
    jacocoAggregation(project(":plugins:thumbnails:dsl"))
    jacocoAggregation(project(":plugins:thumbnails:server"))
    jacocoAggregation(project(":applications:empty-server"))
    jacocoAggregation(project(":applications:examples:musician-renderer"))
}

reporting {
    reports {
        @Suppress("UnstableApiUsage")
        val testCodeCoverageReport by creating(JacocoCoverageReport::class) {
            testSuiteName = "test"
        }
    }
}

tasks.check {
    dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
}