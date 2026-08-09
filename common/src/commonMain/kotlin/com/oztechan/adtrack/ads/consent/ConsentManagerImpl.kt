/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/**
 * Shared consent-gated ads initializer: initialize ads via [platformConsentManager] the first time
 * consent permits, and never again.
 *
 * We never force consent or block on it: whatever the user decides, ads init as soon as
 * [PlatformConsentManager.canRequestAds] allows (the ad SDK then serves personalized or
 * non-personalized ads from the stored consent). Reusable as a standalone consent library.
 */
class ConsentManagerImpl(
    private val platformConsentManager: PlatformConsentManager
) : ConsentManager {

    // Touched only on the main thread (the launch call + UMP callbacks), so no atomics are needed.
    private var adsInitialized = false

    override fun initializeAdsIfPermitted() {
        if (!platformConsentManager.canRequestAds() || adsInitialized) return
        adsInitialized = true
        platformConsentManager.initializeAds()
    }
}
