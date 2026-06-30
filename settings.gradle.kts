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

include(
    ":common",
    ":androidApp",
)
// iOS app lives in `iosApp/` as an Xcode project that links the framework produced by :common.

rootProject.name = "AdTrace"
