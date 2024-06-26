extra["springBootVersion"] = "3.3.0" // Also change below where it cannot use a variable
extra["loggingVersion"] = "6.0.9"
extra["mockkVersion"] = "1.13.11"
extra["springMockkVersion"] = "4.0.2"
extra["equalsVerifierVersion"] = "3.16.1"
extra["slf4jVersion"] = "2.0.13"

plugins {
    kotlin("jvm") version "1.9.23" apply false
    kotlin("plugin.spring") version "1.9.23" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23" apply false
    id("org.jetbrains.kotlin.plugin.noarg") version "1.9.23" apply false
    id("org.sonarqube") version "4.4.1.3373"
    id("org.springframework.boot") version "3.3.0" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
}

sonar {
    properties {
        property("sonar.projectKey", "vibrantnet_ryp")
        property("sonar.organization", "vibrantnet")
    }
}