/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlin.concurrent.thread

/**
 * Gathers UMP (GDPR) consent, then initializes the Mobile Ads SDK once ads may be requested
 * (canRequestAds).
 *
 * We never force consent or block on it: if the user declines, the SDK still serves
 * non-personalized / limited ads automatically from the stored consent signal.
 */
class AdsConsentManager(private val activity: Activity) {

    private val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
    private var adsInitialized = false

    /** Call once at launch (from an Activity — the consent form is shown over it). */
    fun gatherConsentThenInitializeAds() {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // Consent info is up to date; show the form only if the user is required to see it.
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    initializeAdsIfPermitted()
                }
            },
            {
                // Update failed (e.g. offline); fall back to whatever consent is already stored.
                initializeAdsIfPermitted()
            }
        )
        // Returning users who already consented can start ads without waiting for the update.
        initializeAdsIfPermitted()
    }

    private fun initializeAdsIfPermitted() {
        if (!consentInformation.canRequestAds() || adsInitialized) return
        adsInitialized = true
        // Google recommends initializing the Mobile Ads SDK off the main thread, as it does I/O.
        thread { MobileAds.initialize(activity.applicationContext) }
    }
}
