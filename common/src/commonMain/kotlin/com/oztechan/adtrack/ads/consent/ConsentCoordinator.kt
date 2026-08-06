/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/**
 * Shared consent orchestration driven by both platforms: gather consent via [gateway], then invoke
 * [onAdsReady] once (and only once) ads may be requested.
 *
 * We never force consent or block on it: whatever the user decides, [onAdsReady] fires as soon as
 * [ConsentGateway.canRequestAds] allows (the ad SDK then serves personalized or non-personalized
 * ads from the stored consent). This class knows nothing about AdMob — the platform decides what
 * "ads ready" means — which keeps it reusable as a standalone consent library.
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
