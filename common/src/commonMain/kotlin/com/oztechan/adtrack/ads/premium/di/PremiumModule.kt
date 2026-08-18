/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.premium.di

import com.oztechan.adtrack.ads.premium.PremiumManager
import com.oztechan.adtrack.ads.premium.PremiumManagerImpl
import org.koin.dsl.module

val premiumModule = module {
    single<PremiumManager> { PremiumManagerImpl(get()) }
}
