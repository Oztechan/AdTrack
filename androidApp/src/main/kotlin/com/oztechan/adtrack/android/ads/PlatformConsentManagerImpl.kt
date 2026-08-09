/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.oztechan.adtrack.ads.consent.ConsentCallback
import com.oztechan.adtrack.ads.consent.PlatformConsentManager
import kotlin.concurrent.thread

/** Android [PlatformConsentManager] backed by the UMP SDK and the Mobile Ads SDK. */
class PlatformConsentManagerImpl(private val activity: Activity) : PlatformConsentManager {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    override fun canRequestAds(): Boolean = consentInformation.canRequestAds()

    override fun requestConsentInfoUpdate(callback: ConsentCallback) {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            { callback.onCompleted() },
            { callback.onCompleted() }
        )
    }

    override fun loadAndShowFormIfRequired(callback: ConsentCallback) {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { callback.onCompleted() }
    }

    override fun initializeAds() {
        // Google recommends initializing the Mobile Ads SDK off the main thread, as it does I/O.
        thread { MobileAds.initialize(activity.applicationContext) }
    }
}
