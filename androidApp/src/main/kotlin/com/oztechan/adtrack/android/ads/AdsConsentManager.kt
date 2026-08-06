/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.oztechan.adtrack.ads.consent.ConsentCoordinator
import com.oztechan.adtrack.ads.consent.ConsentGateway
import kotlin.concurrent.thread

/**
 * Android consent host: implements the shared [ConsentGateway] over the UMP SDK and drives the
 * shared [ConsentCoordinator], initializing the Mobile Ads SDK once ads may be requested.
 */
class AdsConsentManager(private val activity: Activity) : ConsentGateway {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    // Lazy so `this` is fully constructed before it is handed to the coordinator.
    private val coordinator by lazy {
        ConsentCoordinator(
            gateway = this,
            onAdsReady = {
                // Google recommends initializing the Mobile Ads SDK off the main thread (it does I/O).
                thread { MobileAds.initialize(activity.applicationContext) }
            }
        )
    }

    fun gatherConsentThenInitializeAds() = coordinator.gatherConsentThenInitializeAds()

    override fun canRequestAds(): Boolean = consentInformation.canRequestAds()

    override fun requestConsentInfoUpdate(onComplete: () -> Unit) {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            { onComplete() },
            { onComplete() }
        )
    }

    override fun loadAndShowFormIfRequired(onComplete: () -> Unit) {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { onComplete() }
    }
}
