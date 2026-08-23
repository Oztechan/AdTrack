/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.rewarded.di

import com.oztechan.adtrack.ads.rewarded.RewardedAdConfig
import com.oztechan.adtrack.ads.rewarded.RewardedAdManager
import com.oztechan.adtrack.config.BuildKonfig
import org.koin.dsl.module

val rewardedModule = module {
    single { RewardedAdConfig(BuildKonfig.ADMOB_REWARDED_UNIT_ID) }
    single { RewardedAdManager(get()) }
}
