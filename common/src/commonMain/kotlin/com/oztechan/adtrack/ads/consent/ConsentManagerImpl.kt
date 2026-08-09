/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/**
 * Shared consent orchestration: gather consent via [platformConsentManager], then initialize ads
 * once (and only once) ads may be requested.
 *
 * We never force consent or block on it: whatever the user decides, ads init proceeds as soon as
 * [PlatformConsentManager.canRequestAds] allows (the ad SDK then serves personalized or
 * non-personalized ads from the stored consent). Reusable as a standalone consent library.
 */
class ConsentManagerImpl(
    private val platformConsentManager: PlatformConsentManager
) : ConsentManager {

    // Touched only on the main thread (the launch call + UMP callbacks), so no atomics are needed.
    private var adsInitialized = false

    override fun gatherConsentThenInitializeAds() {
        platformConsentManager.requestConsentInfoUpdate(object : ConsentCallback {
            override fun onCompleted() {
                platformConsentManager.loadAndShowFormIfRequired(object : ConsentCallback {
                    override fun onCompleted() = initializeAdsIfPermitted()
                })
            }
        })
        // Returning users who already consented can start ads without waiting for the update.
        initializeAdsIfPermitted()
    }

    private fun initializeAdsIfPermitted() {
        if (!platformConsentManager.canRequestAds() || adsInitialized) return
        adsInitialized = true
        platformConsentManager.initializeAds()
    }
}
