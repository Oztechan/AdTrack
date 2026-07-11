/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    libs.plugins.apply {
        alias(kotlinMultiplatform)
        alias(androidLibrary)
        alias(jetbrainsCompose)
        alias(kotlinPluginCompose)
        alias(serialization)
        alias(buildKonfig)
        alias(mokkery)
    }
}

// region OAuth secrets — read from secret.properties / gradle property / env, with safe fallbacks
val secretProps = Properties().apply {
    val file = rootProject.file("secret.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun secret(key: String, default: String): String =
    secretProps.getProperty(key) ?: project.findProperty(key)?.toString() ?: System.getenv(key) ?: default

// Native OAuth clients are per-platform (separate Google Cloud client ids for Android & iOS).
val oauthClientIdAndroid = secret(
    "GOOGLE_OAUTH_CLIENT_ID_ANDROID",
    "YOUR_ANDROID_OAUTH_CLIENT_ID.apps.googleusercontent.com"
)
val oauthClientIdIos = secret("GOOGLE_OAUTH_CLIENT_ID_IOS", "YOUR_IOS_OAUTH_CLIENT_ID.apps.googleusercontent.com")
// endregion

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "common"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            libs.submob.apply {
                implementation(logmob)
            }

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.uiToolingPreview)

            libs.common.apply {
                implementation(navigationCompose)
                implementation(lifecycleViewmodel)
                implementation(lifecycleViewmodelCompose)
                implementation(lifecycleRuntimeCompose)
                implementation(koinCore)
                implementation(koinCompose)
                implementation(koinComposeViewmodel)
                implementation(ktorCore)
                implementation(ktorContentNegotiation)
                implementation(ktorJson)
                implementation(ktorLogging)
                implementation(ktorAuth)
                implementation(coroutines)
                implementation(kotlinXDateTime)
                implementation(multiplatformSettings)
                implementation(kermit)
                implementation(sha2)
            }
        }
        commonTest.dependencies {
            libs.common.apply {
                implementation(test)
                implementation(coroutinesTest)
                implementation(ktorClientMock)
                implementation(multiplatformSettingsTest)
            }
        }
        androidMain.dependencies {
            libs.android.apply {
                implementation(ktor)
                implementation(activityCompose)
                implementation(koinAndroid)
                implementation(browser)
                implementation(securityCrypto)
            }
        }
        iosMain.dependencies {
            implementation(libs.ios.ktor)
        }
    }
}

android {
    namespace = ProjectSettings.PROJECT_ID
    compileSdk = ProjectSettings.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = ProjectSettings.MIN_SDK_VERSION
    }

    compileOptions {
        sourceCompatibility = ProjectSettings.JAVA_VERSION
        targetCompatibility = ProjectSettings.JAVA_VERSION
    }
}

buildkonfig {
    packageName = "${ProjectSettings.PROJECT_ID}.config"

    defaultConfigs {
        // Fallback (Android client id); overridden per target below so shared code can keep
        // referencing BuildKonfig.GOOGLE_OAUTH_CLIENT_ID and get the platform-correct value.
        buildConfigField(STRING, "GOOGLE_OAUTH_CLIENT_ID", oauthClientIdAndroid)
        buildConfigField(STRING, "OAUTH_REDIRECT_SCHEME", ProjectSettings.PROJECT_ID)
    }

    targetConfigs {
        create("android") {
            buildConfigField(STRING, "GOOGLE_OAUTH_CLIENT_ID", oauthClientIdAndroid)
        }
        create("iosX64") {
            buildConfigField(STRING, "GOOGLE_OAUTH_CLIENT_ID", oauthClientIdIos)
        }
        create("iosArm64") {
            buildConfigField(STRING, "GOOGLE_OAUTH_CLIENT_ID", oauthClientIdIos)
        }
        create("iosSimulatorArm64") {
            buildConfigField(STRING, "GOOGLE_OAUTH_CLIENT_ID", oauthClientIdIos)
        }
    }
}

dependencies {
    // ComposeViewAdapter (the @Preview renderer) must be on this module's Android debug classpath,
    // since Android Studio renders a module's previews using that module's own classpath.
    debugImplementation(compose.uiTooling)
}
