import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension

extra["springBootVersion"] = "3.3.2" // Also change below where it cannot use a variable
extra["loggingVersion"] = "6.0.9"
extra["mockkVersion"] = "1.13.12"
extra["springMockkVersion"] = "4.0.2"
extra["equalsVerifierVersion"] = "3.16.1"
extra["slf4jVersion"] = "2.0.16"

plugins {
    kotlin("jvm") version "1.9.23" apply false
    kotlin("plugin.spring") version "1.9.23" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23" apply false
    id("org.jetbrains.kotlin.plugin.noarg") version "1.9.23" apply false
    id("org.sonarqube") version "5.1.0.4882"
    id("org.springframework.boot") version "3.3.2" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
    id("org.owasp.dependencycheck") version "10.0.3" apply true
}

buildscript {
    repositories {
        mavenCentral()
    }
}

val nvdApiKey: String? by project

allprojects {
    apply {
        plugin("org.owasp.dependencycheck")
    }
    configure<DependencyCheckExtension> {
        nvd.apiKey = nvdApiKey
        failBuildOnCVSS = 7.0f
        data.directory = "${rootDir}/.gradle/dependency-check-data/"
        analyzers.assemblyEnabled = false
        analyzers.nodeAuditEnabled = false
        analyzers.nodeEnabled = false
    }
}

sonar {
    properties {
        property("sonar.projectKey", "vibrantnet_ryp")
        property("sonar.organization", "vibrantnet")
    }
}