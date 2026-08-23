/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.oztechan.adtrack.ads.interstitial.InterstitialAdConfig
import com.oztechan.adtrack.ads.interstitial.PlatformInterstitialAd

/**
 * Android [PlatformInterstitialAd]: preloads an [InterstitialAd] and shows it from the current
 * Activity. The loaded ad is cleared once shown/dismissed so [isReady] reflects a single use. Lives
 * in the app module alongside the SDK.
 */
class AndroidInterstitialAd(
    private val context: Context,
    private val activityHolder: CurrentActivityHolder,
    private val config: InterstitialAdConfig
) : PlatformInterstitialAd {

    private var interstitialAd: InterstitialAd? = null

    override fun load() {
        InterstitialAd.load(
            context,
            config.adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            interstitialAd = null
                        }
                    }
                    interstitialAd = ad
                }
            }
        )
    }

    override fun isReady(): Boolean = interstitialAd != null

    override fun show() {
        val activity = activityHolder.current ?: return
        interstitialAd?.show(activity)
    }
}
