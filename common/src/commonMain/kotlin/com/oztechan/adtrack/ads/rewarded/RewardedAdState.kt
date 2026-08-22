/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.rewarded

/** UI-facing status of the opt-in rewarded ad. */
enum class RewardedAdState {
    /** Nothing in flight; the offer can be shown. */
    IDLE,

    /** The ad is loading or on screen. */
    LOADING,

    /** The ad failed to load or show. */
    FAILED
}
