/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads.di

import com.oztechan.adtrack.ads.banner.AndroidBannerFactory
import com.oztechan.adtrack.android.ads.AndroidBannerFactoryImpl
import org.koin.dsl.module

/** Android-only ad SDK bindings; the shared banner composable injects [AndroidBannerFactory]. */
val androidAdsModule = module {
    single<AndroidBannerFactory> { AndroidBannerFactoryImpl() }
}
