/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.oztechan.adtrack.ads.rewarded.PlatformRewardedAd
import com.oztechan.adtrack.ads.rewarded.RewardedAdConfig
import com.oztechan.adtrack.ads.rewarded.RewardedAdManager

/**
 * Android [PlatformRewardedAd]: loads a [RewardedAd] and shows it from the current Activity, reporting
 * its lifecycle back into the shared [RewardedAdManager]. Lives in the app module alongside the SDK.
 */
class AndroidRewardedAd(
    private val context: Context,
    private val activityHolder: CurrentActivityHolder,
    private val rewardedAdManager: RewardedAdManager,
    private val config: RewardedAdConfig
) : PlatformRewardedAd {

    override fun show() {
        // A rewarded ad can only be shown from an Activity; bail if none is resumed.
        if (activityHolder.current == null) {
            rewardedAdManager.onFailed()
            return
        }
        rewardedAdManager.onLoading()

        RewardedAd.load(
            context,
            config.adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) = rewardedAdManager.onFailed()

                override fun onAdLoaded(ad: RewardedAd) {
                    val activity = activityHolder.current
                    if (activity == null) {
                        rewardedAdManager.onFailed()
                        return
                    }
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() = rewardedAdManager.onFinished()
                        override fun onAdFailedToShowFullScreenContent(error: AdError) = rewardedAdManager.onFailed()
                    }
                    // The reward amount is fixed on our side, so the earned item is ignored.
                    ad.show(activity) { rewardedAdManager.onRewardEarned() }
                }
            }
        )
    }
}
