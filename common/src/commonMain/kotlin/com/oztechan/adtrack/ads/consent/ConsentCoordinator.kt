/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/**
 * Drives the consent flow, then invokes [onAdsReady] once (and only once) ads may be requested.
 *
 * We never force consent or block on it: whatever the user decides, ads init proceeds as soon as
 * [ConsentGateway.canRequestAds] allows (the SDK then serves personalized or non-personalized ads
 * from the stored consent). This is the reusable core intended to move into a shared library.
 */
class ConsentCoordinator(
    private val gateway: ConsentGateway,
    private val onAdsReady: () -> Unit
) {
    // Touched only on the main thread (the launch call + UMP callbacks), so no atomics are needed.
    private var adsReady = false

    fun gatherConsentThenInitializeAds() {
        gateway.requestConsentInfoUpdate {
            gateway.loadAndShowFormIfRequired {
                notifyAdsReadyIfPermitted()
            }
        }
        // Returning users who already consented can start ads without waiting for the update.
        notifyAdsReadyIfPermitted()
    }

    private fun notifyAdsReadyIfPermitted() {
        if (!gateway.canRequestAds()) return
        if (adsReady) return
        adsReady = true
        onAdsReady()
    }
}
