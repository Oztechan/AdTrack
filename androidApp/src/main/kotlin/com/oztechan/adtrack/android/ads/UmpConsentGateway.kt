/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.oztechan.adtrack.ads.consent.ConsentGateway

/** [ConsentGateway] backed by Google's UMP SDK — thin platform glue over the UMP static API. */
class UmpConsentGateway(private val activity: Activity) : ConsentGateway {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

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
