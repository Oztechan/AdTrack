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
    }
}

// region SubMob shared libraries
// Built from the submodule in submodule/, pinned to a release tag, so a library change arrives here
// as a reviewable pointer bump instead of a published version. The catalog entry carries no
// version, so this build is the only source of the library. Substitution is explicit because
// vanniktech sets the module group late, which the automatic composite substitution can miss.
// A composite build allows only one Android Gradle plugin version, so AdTrack and the tag it
// points at must stay on the same one.
listOf(
    "LogMob",
).forEach { dirName ->
    val artifact = dirName.lowercase()
    val dir = file("submodule/$dirName")
    require(dir.resolve("settings.gradle.kts").isFile) {
        "Submodule $dirName is missing. Run: git submodule update --init --recursive"
    }
    includeBuild(dir) {
        dependencySubstitution {
            substitute(module("com.github.submob:$artifact")).using(project(":$artifact"))
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
