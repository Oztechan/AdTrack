/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.session

import com.oztechan.adtrack.core.storage.PreferenceStorage

/**
 * Increments and persists the launch count once, when first created. As a Koin singleton this object
 * is built once per cold start, so [count] reflects the current session number for the whole launch.
 * Backed by [PreferenceStorage] (not the Keychain), so the count is wiped on uninstall.
 */
class SessionManagerImpl(storage: PreferenceStorage) : SessionManager {

    override val count: Long = ((storage.getLong(KEY_SESSION_COUNT) ?: 0L) + 1)
        .also { storage.putLong(KEY_SESSION_COUNT, it) }

    private companion object {
        const val KEY_SESSION_COUNT = "session_count"
    }
}
