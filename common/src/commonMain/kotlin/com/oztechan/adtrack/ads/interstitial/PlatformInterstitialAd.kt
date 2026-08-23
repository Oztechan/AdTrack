/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.interstitial

/**
 * Preloads and shows an interstitial ad, implemented per platform (Android `InterstitialAd` / iOS
 * `GADInterstitialAd`). The ad SDK stays in the app modules; the shared [InterstitialManager] decides
 * *whether* to show and drives this seam. All calls are synchronous, so nothing crosses the boundary
 * as a callback.
 */
interface PlatformInterstitialAd {
    /** Start loading an interstitial so it is ready for the next transition. */
    fun load()

    /** Whether a loaded interstitial is ready to show right now. */
    fun isReady(): Boolean

    /** Show the loaded interstitial (no-op if none is ready). */
    fun show()
}
