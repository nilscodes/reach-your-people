import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	kotlin("jvm")
	kotlin("plugin.spring")
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

jacoco {
	toolVersion = "0.8.11"
}

val loggingVersion: String by rootProject.extra
val mockkVersion: String by rootProject.extra
val equalsVerifierVersion: String by rootProject.extra
val slf4jVersion: String by rootProject.extra
val libsodiumJnaVersion = "1.2.0-NEWM" // Might be able to use the non-customized version of this JNA library
val bouncyCastleVersion = "1.78.1" // We can likely skip using bouncycastle if we write some custom hex/byte array concatenation and decoding/encoding methods

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.github.oshai:kotlin-logging-jvm:$loggingVersion")
	implementation("org.slf4j:slf4j-api:$slf4jVersion")
	implementation("org.postgresql:postgresql")
	implementation("io.newm:com.muquit.libsodiumjna.libsodium-jna:$libsodiumJnaVersion")
	implementation("org.bouncycastle:bcprov-jdk18on:$bouncyCastleVersion")
	implementation(project(":core-api"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("io.mockk:mockk:$mockkVersion")
	testImplementation("com.h2database:h2")
	testImplementation("com.squareup.okhttp3:mockwebserver")
	testImplementation("nl.jqno.equalsverifier:equalsverifier:$equalsVerifierVersion")
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