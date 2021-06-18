val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val prometeus_version: String by project
val junit_version: String by project
val mockk_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
}

group = "me.lasta"
version = "0.0.1"
application {
    mainClass.set("me.lasta.ApplicationKt")
}

repositories {
    mavenCentral()

    // for gt-shapefile
    maven(url = "https://repo.osgeo.org/repository/release/")
    maven(url = "https://repo.osgeo.org/repository/snapshot/")
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.ktor:ktor-metrics-micrometer:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("org.geotools:gt-shapefile:26-SNAPSHOT@pom")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_version")
    testImplementation("io.mockk:mockk:$mockk_version")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        showStandardStreams = true
        events("started", "skipped", "passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
