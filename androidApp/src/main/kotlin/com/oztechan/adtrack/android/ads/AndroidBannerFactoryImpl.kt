/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.oztechan.adtrack.ads.banner.AndroidBannerFactory

/**
 * Android [AndroidBannerFactory]: builds an anchored adaptive [AdView] sized to the current screen
 * width and immediately requests an ad. Lives in the app module alongside the Mobile Ads SDK.
 */
class AndroidBannerFactoryImpl : AndroidBannerFactory {

    override fun create(context: Context, adUnitId: String): View = AdView(context).apply {
        setAdSize(adaptiveSize(context))
        this.adUnitId = adUnitId
        loadAd(AdRequest.Builder().build())
    }

    // The anchored-adaptive helpers are deprecated in play-services-ads 25.x in favour of the new
    // auto-height adaptive banner; the helper still returns the correct anchored size, so we keep it
    // until that migration is done as its own step.
    @Suppress("DEPRECATION")
    private fun adaptiveSize(context: Context): AdSize {
        val metrics = context.resources.displayMetrics
        val widthDp = (metrics.widthPixels / metrics.density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
    }
}
