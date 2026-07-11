/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        // SubMob shared libraries publish -SNAPSHOT builds here; releases resolve via mavenCentral().
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
    }
}

// region SubMob shared libraries
// Co-develop from the sibling Oztechan/SubMob checkouts when present (edits show up instantly via a
// Gradle composite build); otherwise — CI, or a clone without the siblings — resolve the published
// versions declared in gradle/libs.versions.toml. Substitution is explicit because vanniktech sets
// the module group late, which the automatic composite substitution can miss.
listOf(
    "LogMob",
).forEach { dirName ->
    val artifact = dirName.lowercase()
    val dir = file("../SubMob/$dirName")
    if (dir.exists()) {
        includeBuild(dir) {
            dependencySubstitution {
                substitute(module("com.github.submob:$artifact")).using(project(":$artifact"))
            }
        }
    }
}
// endregion

include(
    ":common",
    ":androidApp",
)
// iOS app lives in `iosApp/` as an Xcode project that links the framework produced by :common.

rootProject.name = "AdTrack"
