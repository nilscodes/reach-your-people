plugins {
    kotlin("jvm")
    id("io.spring.dependency-management")
}

group = "io.vibrantnet.ryp"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Uses the same version as our spring boot implementation
    implementation("org.springframework:spring-web")
    // Uses the same version as our spring boot implementation
    implementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

val springBootVersion: String by rootProject.extra

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}