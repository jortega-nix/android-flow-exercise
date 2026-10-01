plugins {
    // Downloads a matching JDK automatically if the local one is not 17,
    // so the build never fails with "unsupported class file major version".
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "flow-exercise"
