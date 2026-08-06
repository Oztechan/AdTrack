/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import com.google.android.gms.ads.MobileAds
import com.oztechan.adtrack.ads.consent.ConsentCoordinator
import kotlin.concurrent.thread

/**
 * Wires the UMP-backed [UmpConsentGateway] to the shared [ConsentCoordinator] for this Activity:
 * gather consent, then initialize the Mobile Ads SDK once ads may be requested.
 */
class AdsConsentManager(activity: Activity) {

    private val coordinator = ConsentCoordinator(
        gateway = UmpConsentGateway(activity),
        onAdsReady = {
            // Google recommends initializing the Mobile Ads SDK off the main thread, as it does I/O.
            thread { MobileAds.initialize(activity.applicationContext) }
        }
    )

    fun gatherConsentThenInitializeAds() = coordinator.gatherConsentThenInitializeAds()
}
