/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */
package config.key

enum class Key(
    val default: String = ""
) {
    ANDROID_KEY_STORE_PATH("release.keystore"),
    ANDROID_STORE_PASSWORD,
    ANDROID_KEY_ALIAS,
    ANDROID_KEY_PASSWORD,

    // AdMob App ID for the Android manifest (per-platform: the iOS app has its own App ID). Defaults
    // to Google's public Android test App ID so debug builds work without secrets; the real one comes
    // from secret.properties / env.
    ADMOB_APP_ID_ANDROID("ca-app-pub-3940256099942544~3347511713")
}
