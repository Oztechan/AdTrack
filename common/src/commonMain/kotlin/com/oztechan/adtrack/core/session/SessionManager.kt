/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.session

/**
 * Tracks how many times the app has been launched. [count] is a pure read of the persisted number of
 * sessions (0 before the first [registerSession]); [registerSession] advances it and is called once
 * per cold start. Backed by uninstall-scoped storage, so the count resets on reinstall. Used to give
 * brand-new users an ad-free grace period.
 */
interface SessionManager {

    /** Sessions (app launches) recorded so far. Reading this never changes it. */
    val count: Long

    /** Record a new session. Call exactly once per app launch (from `initKoin`). */
    fun registerSession()
}
