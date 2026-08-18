/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.premium

import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

/**
 * Tracks whether the user currently has an ad-free (premium) session. Ad UI observes [isPremium] to
 * hide itself. Premium is a timed window today (granted by the rewarded ad); a permanent paid Pro
 * tier can later grant a far-future window through the same mechanism.
 */
interface PremiumManager {
    /** Whether ads should currently be hidden. Observe to react to grants and expiry. */
    val isPremium: StateFlow<Boolean>

    /** Grant (or extend) a timed ad-free window — used by the rewarded ad. Windows stack. */
    fun grantPremium(duration: Duration)

    /** Recompute against the current time; call on app start/resume to expire lapsed windows. */
    fun refresh()
}
