/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.storage

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings

/**
 * Thin secure key/value store. Backed by EncryptedSharedPreferences on Android and the
 * Keychain on iOS (see the platform-specific [Settings] provided in the platform Koin module).
 * Reads are defensive: a backing-store failure (e.g. Keystore invalidation after a lock change)
 * returns null rather than crashing, which the caller treats as "signed out".
 */
class SecureStorage(private val settings: Settings) {

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun getString(key: String): String? = try {
        settings.getStringOrNull(key)
    } catch (e: Exception) {
        Logger.e(e) { "SecureStorage read failed for $key" }
        null
    }

    fun putString(key: String, value: String) {
        settings.putString(key, value)
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun getLong(key: String): Long? = try {
        settings.getLongOrNull(key)
    } catch (e: Exception) {
        Logger.e(e) { "SecureStorage read failed for $key" }
        null
    }

    fun putLong(key: String, value: Long) {
        settings.putLong(key, value)
    }

    fun remove(key: String) {
        settings.remove(key)
    }

    fun clear() {
        settings.clear()
    }
}
