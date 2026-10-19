/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.admob.di

import com.oztechan.adtrack.core.network.di.AUTH_CLIENT
import com.oztechan.adtrack.data.admob.api.AdMobApi
import com.oztechan.adtrack.data.admob.api.AdMobApiImpl
import com.oztechan.adtrack.domain.PeriodCalculator
import com.oztechan.adtrack.domain.repository.RevenueRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val adMobModule = module {
    single<AdMobApi> { AdMobApiImpl(get(AUTH_CLIENT)) }
    single { PeriodCalculator() }
    // Provided concretely (not bound to RevenueRepository): the app-wide RevenueRepository binding is
    // the demo overlay in demoModule, which delegates here when not in demo mode.
    singleOf(::RevenueRepositoryImpl)
}
