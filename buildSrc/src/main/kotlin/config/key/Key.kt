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
    ANDROID_KEY_PASSWORD
}
