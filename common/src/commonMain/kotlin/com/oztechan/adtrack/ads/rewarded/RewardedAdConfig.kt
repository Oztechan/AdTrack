/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.rewarded

/**
 * Rewarded-ad configuration read from `BuildKonfig` in `common` (the only module that can see it) and
 * handed to the per-platform [PlatformRewardedAd] impls, so the ad unit id has a single source.
 */
data class RewardedAdConfig(val adUnitId: String)
