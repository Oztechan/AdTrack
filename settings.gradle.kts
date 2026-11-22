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
        // Scoped to SubMob snapshots so Gradle never queries it for anything else.
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
            mavenContent { snapshotsOnly() }
            content { includeGroup("com.github.submob") }
        }
    }
}

// region SubMob shared libraries
// Built from the submodule in submob/, pinned to a release tag, so a library change arrives here
// as a reviewable pointer bump instead of a published version. A clone without submodules falls
// back to the versions in gradle/libs.versions.toml. Substitution is explicit because vanniktech
// sets the module group late, which the automatic composite substitution can miss.
// A composite build allows only one Android Gradle plugin version, so AdTrack and the tag it
// points at must stay on the same one.
listOf(
    "LogMob",
).forEach { dirName ->
    val artifact = dirName.lowercase()
    val dir = file("submob/$dirName")
    if (dir.resolve("settings.gradle.kts").isFile) {
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
