/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.interstitial.di

import com.oztechan.adtrack.ads.interstitial.InterstitialAdConfig
import com.oztechan.adtrack.ads.interstitial.InterstitialManager
import com.oztechan.adtrack.config.BuildKonfig
import org.koin.dsl.module

val interstitialModule = module {
    single { InterstitialAdConfig(BuildKonfig.ADMOB_INTERSTITIAL_UNIT_ID) }
    single { InterstitialManager(get(), get()) }
}
