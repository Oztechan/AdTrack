/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.oztechan.adtrack.ads.consent.ConsentManagerImpl
import com.oztechan.adtrack.ads.consent.PlatformConsentManager
import kotlin.concurrent.thread

/**
 * Android [PlatformConsentManager]: runs the UMP consent flow, driving the shared
 * [ConsentManagerImpl] to initialize the Mobile Ads SDK once ads may be requested.
 */
class PlatformConsentManagerImpl(private val activity: Activity) : PlatformConsentManager {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    /** Call once at launch (from an Activity — the consent form is shown over it). */
    fun gatherConsentThenInitializeAds() {
        // Local so the shared guard lives only for the flow (kept alive by the UMP callbacks).
        val consentManager = ConsentManagerImpl(this)
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    consentManager.initializeAdsIfPermitted()
                }
            },
            {
                consentManager.initializeAdsIfPermitted()
            }
        )
        // Returning users who already consented can start ads without waiting for the update.
        consentManager.initializeAdsIfPermitted()
    }

    override fun canRequestAds(): Boolean = consentInformation.canRequestAds()

    override fun initializeAds() {
        // Google recommends initializing the Mobile Ads SDK off the main thread, as it does I/O.
        thread { MobileAds.initialize(activity.applicationContext) }
    }
}
