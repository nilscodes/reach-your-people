import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("io.spring.dependency-management")
    jacoco
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
val equalsVerifierVersion: String by rootProject.extra
val jsonAssertVersion = "1.5.3"

dependencies {
    // Uses the same version as our spring boot implementation
    compileOnly("org.springframework:spring-web")
    compileOnly("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.springframework.boot:spring-boot-starter-amqp")

    implementation("io.github.oshai:kotlin-logging-jvm:$loggingVersion")
    // Uses the same version as our spring boot implementation
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("org.skyscreamer:jsonassert:$jsonAssertVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-aop")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-amqp")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:$equalsVerifierVersion")
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required = true
        xml.outputLocation = layout.buildDirectory.file("jacoco.xml")
        csv.required = false
        html.required = false
    }
}