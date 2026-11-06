/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.session

import com.oztechan.adtrack.core.storage.PreferenceStorage

/**
 * Persists the launch count in [PreferenceStorage] (not the Keychain), so it is wiped on uninstall.
 * Construction has no side effect: [count] purely reads the stored value, and [registerSession] is
 * the only thing that advances it — so reading the count from anywhere is always safe.
 */
class SessionManagerImpl(private val storage: PreferenceStorage) : SessionManager {

    override val count: Long
        get() = storage.getLong(KEY_SESSION_COUNT) ?: 0L

    override fun registerSession() {
        storage.putLong(KEY_SESSION_COUNT, count + 1)
    }

    private companion object {
        const val KEY_SESSION_COUNT = "session_count"
    }
}
