/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.storage

import com.russhwolf.settings.Settings

/**
 * App-sandbox key/value store for non-secret, resettable state (currently the premium window).
 *
 * The point is that it is wiped when the app is uninstalled, so premium resets on reinstall. On iOS
 * that means NOT using the Keychain (which survives uninstall) — it is backed by NSUserDefaults
 * instead. On Android all app storage is already wiped on uninstall, so it simply reuses the app's
 * store. Reads don't need the defensive handling [SecureStorage] uses, because these backing stores
 * can't fail the way the Keychain can.
 */
class PreferenceStorage(private val settings: Settings) {

    fun getLong(key: String): Long? = settings.getLongOrNull(key)

    fun putLong(key: String, value: Long) {
        settings.putLong(key, value)
    }

    fun remove(key: String) {
        settings.remove(key)
    }
}
