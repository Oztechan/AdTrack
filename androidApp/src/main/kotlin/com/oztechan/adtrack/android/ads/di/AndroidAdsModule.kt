/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads.di

import com.oztechan.adtrack.ads.banner.AndroidBannerFactory
import com.oztechan.adtrack.ads.interstitial.PlatformInterstitialAd
import com.oztechan.adtrack.ads.rewarded.PlatformRewardedAd
import com.oztechan.adtrack.android.ads.AndroidBannerFactoryImpl
import com.oztechan.adtrack.android.ads.AndroidInterstitialAd
import com.oztechan.adtrack.android.ads.AndroidRewardedAd
import com.oztechan.adtrack.android.ads.CurrentActivityHolder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Android-only ad SDK bindings; the shared ad code injects these interfaces. */
val androidAdsModule = module {
    single<AndroidBannerFactory> { AndroidBannerFactoryImpl() }
    single { CurrentActivityHolder() }
    single<PlatformRewardedAd> {
        AndroidRewardedAd(
            context = androidContext(),
            activityHolder = get(),
            rewardedAdManager = get(),
            config = get()
        )
    }
    single<PlatformInterstitialAd> {
        AndroidInterstitialAd(
            context = androidContext(),
            activityHolder = get(),
            config = get()
        )
    }
}
