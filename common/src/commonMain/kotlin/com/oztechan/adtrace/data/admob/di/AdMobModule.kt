/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.admob.di

import com.oztechan.adtrace.core.network.di.AUTH_CLIENT
import com.oztechan.adtrace.data.admob.api.AdMobApi
import com.oztechan.adtrace.data.admob.api.AdMobApiImpl
import com.oztechan.adtrace.domain.PeriodCalculator
import com.oztechan.adtrace.domain.repository.RevenueRepository
import com.oztechan.adtrace.domain.repository.RevenueRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val adMobModule = module {
    single<AdMobApi> { AdMobApiImpl(get(AUTH_CLIENT)) }
    single { PeriodCalculator() }
    singleOf(::RevenueRepositoryImpl) bind RevenueRepository::class
}
