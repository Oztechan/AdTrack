/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.di

import com.oztechan.adtrack.ads.interstitial.di.interstitialModule
import com.oztechan.adtrack.ads.premium.di.premiumModule
import com.oztechan.adtrack.ads.rewarded.di.rewardedModule
import com.oztechan.adtrack.core.network.di.networkModule
import com.oztechan.adtrack.data.admob.di.adMobModule
import com.oztechan.adtrack.data.auth.di.authModule
import com.oztechan.adtrack.ui.di.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        platformModule,
        networkModule,
        authModule,
        adMobModule,
        premiumModule,
        rewardedModule,
        interstitialModule,
        viewModelModule
    )
}
