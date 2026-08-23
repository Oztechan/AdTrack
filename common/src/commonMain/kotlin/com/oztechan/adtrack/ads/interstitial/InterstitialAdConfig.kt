/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.interstitial

/**
 * Interstitial configuration read from `BuildKonfig` in `common` (the only module that can see it) and
 * handed to the per-platform [PlatformInterstitialAd] impls, so the ad unit id has a single source.
 */
data class InterstitialAdConfig(val adUnitId: String)
