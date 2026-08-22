/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.rewarded

/**
 * Loads and shows a rewarded ad, implemented per platform (Android `RewardedAd` / iOS
 * `GADRewardedAd`). The platform impl reports its lifecycle back into the shared [RewardedAdManager],
 * so the reward decision and UI state stay shared — no callback crosses the platform boundary.
 */
interface PlatformRewardedAd {
    /** User-initiated: load, then show the rewarded ad. */
    fun show()
}
