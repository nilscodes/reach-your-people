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

val loggingVersion: String by rootProject.extra
val mockkVersion: String by rootProject.extra

dependencies {
    // Uses the same version as our spring boot implementation
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("io.github.oshai:kotlin-logging-jvm:$loggingVersion")
    // Uses the same version as our spring boot implementation
    implementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.mockk:mockk:$mockkVersion")
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