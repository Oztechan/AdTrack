/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.session

/**
 * Counts how many times the app has been launched. [count] is the current session number, 1 on the
 * very first launch and increasing by one each cold start. Backed by uninstall-scoped storage, so it
 * resets on reinstall. Used to give brand-new users an ad-free grace period.
 */
interface SessionManager {
    val count: Long
}
