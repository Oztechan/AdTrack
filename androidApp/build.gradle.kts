/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */
plugins {
    libs.plugins.apply {
        alias(androidApplication)
        alias(kotlinAndroid)
        alias(jetbrainsCompose)
        alias(kotlinPluginCompose)
        alias(googleServices)
    }
}

android {
    namespace = "${ProjectSettings.PROJECT_ID}.android"
    compileSdk = ProjectSettings.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = ProjectSettings.PROJECT_ID
        minSdk = ProjectSettings.MIN_SDK_VERSION
        targetSdk = ProjectSettings.TARGET_SDK_VERSION
        versionCode = ProjectSettings.getVersionCode(project)
        versionName = ProjectSettings.getVersionName(project)

        // Must match OAUTH_REDIRECT_SCHEME so the manifest redirect intent-filter resolves.
        manifestPlaceholders["oauthRedirectScheme"] = "com.oztechan.adtrace"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = ProjectSettings.JAVA_VERSION
        targetCompatibility = ProjectSettings.JAVA_VERSION
    }
}

dependencies {
    implementation(project(Modules.COMMON))
    implementation(compose.runtime)
    implementation(libs.android.activityCompose)
    implementation(libs.android.koinAndroid)
    // Renders @Preview composables in the Android Studio preview pane.
    debugImplementation(compose.uiTooling)
    implementation(compose.components.uiToolingPreview)
    implementation(platform(libs.android.firebaseBom))
    implementation(libs.android.firebaseAnalytics)
}
