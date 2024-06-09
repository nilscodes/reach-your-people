plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "reach-your-people"
include("core-verification")
include("core-subscription")
include("core-publishing")
include("core-api")
include("core-redirect")
include("integrations:vibrant")

System.setProperty("sonar.gradle.skipCompile", "true") // Skip implicit compile because it is deprecated - cannot be set via build.gradle.kts yet