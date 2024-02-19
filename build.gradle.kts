extra["springBootVersion"] = "3.2.2" // Also change below where it cannot use a variable
extra["loggingVersion"] = "3.0.5"
extra["mockkVersion"] = "1.13.9"
extra["equalsVerifierVersion"] = "3.15.1"

plugins {
    kotlin("jvm") version "1.9.22" apply false
    kotlin("plugin.spring") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.10" apply false
    id("org.jetbrains.kotlin.plugin.noarg") version "1.6.10" apply false
    id("org.sonarqube") version "4.4.1.3373"
    id("org.springframework.boot") version "3.2.2" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}

sonar {
    properties {
        property("sonar.projectKey", "vibrantnet_ryp")
        property("sonar.organization", "vibrantnet")
    }
}