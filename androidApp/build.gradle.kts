/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */
import config.BuildType
import config.key.Key
import config.key.secret

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
        manifestPlaceholders["oauthRedirectScheme"] = "com.oztechan.adtrack"
    }

    signingConfigs {
        create(BuildType.release) {
            storeFile = file(secret(Key.ANDROID_KEY_STORE_PATH))
            storePassword = secret(Key.ANDROID_STORE_PASSWORD)
            keyAlias = secret(Key.ANDROID_KEY_ALIAS)
            keyPassword = secret(Key.ANDROID_KEY_PASSWORD)
        }
    }

    buildTypes {
        getByName(BuildType.release) {
            signingConfig = signingConfigs.getByName(BuildType.release)
            isMinifyEnabled = false
        }

        getByName(BuildType.debug) {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = ProjectSettings.JAVA_VERSION
        targetCompatibility = ProjectSettings.JAVA_VERSION
    }
}

dependencies {
    libs.submob.apply {
        implementation(logmob)
    }

    implementation(project(Modules.COMMON))
    implementation(compose.runtime)
    implementation(libs.android.activityCompose)
    implementation(libs.android.koinAndroid)
    implementation(libs.android.splashScreen)
    // Renders @Preview composables in the Android Studio preview pane.
    debugImplementation(compose.uiTooling)
    implementation(compose.components.uiToolingPreview)
    implementation(platform(libs.android.firebaseBom))
    implementation(libs.android.firebaseAnalytics)
}
