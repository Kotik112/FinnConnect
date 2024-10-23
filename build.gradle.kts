import org.gradle.api.tasks.testing.logging.TestExceptionFormat

val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val h2_version: String by project
val postgres_version: String by project
val ktor_version: String by project

plugins {
    kotlin("jvm") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0-rc-1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

//    val isDevelopment: Boolean = project.ext.has("development")
    val isDevelopment: Boolean = true
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    // ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    // Databases
    implementation("com.h2database:h2:$h2_version")
    implementation("org.postgresql:postgresql:$postgres_version")
    // ktor server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-thymeleaf-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    // JWT implementation
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    // OAuth2
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    // Logging
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("io.ktor:ktor-server-swagger-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-core:5.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    // TestContainers
    testImplementation("org.testcontainers:testcontainers:1.20.1")
    testImplementation("org.testcontainers:postgresql:1.17.3")
    implementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        showStandardStreams = true

        events("passed", "skipped", "failed")

        // Set the display granularity of the output. Options: all, summary
        exceptionFormat = TestExceptionFormat.FULL

        // Show test output for tests that failed
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
