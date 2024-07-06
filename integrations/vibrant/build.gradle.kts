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

val loggingVersion: String by rootProject.extra
val mockkVersion: String by rootProject.extra
val equalsVerifierVersion: String by rootProject.extra
val slf4jVersion: String by rootProject.extra
val springMockkVersion: String by rootProject.extra

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.github.oshai:kotlin-logging-jvm:$loggingVersion")
	implementation("org.slf4j:slf4j-api:$slf4jVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("io.mockk:mockk:$mockkVersion")
	testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
	testImplementation("nl.jqno.equalsverifier:equalsverifier:$equalsVerifierVersion")
	testImplementation("com.h2database:h2")
	testImplementation("com.squareup.okhttp3:mockwebserver")
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
