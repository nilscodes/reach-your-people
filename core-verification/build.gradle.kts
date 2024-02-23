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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.github.microutils:kotlin-logging-jvm:$loggingVersion")
	implementation("org.postgresql:postgresql")
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