/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.admob.di

import com.oztechan.adtrack.core.network.di.AUTH_CLIENT
import com.oztechan.adtrack.data.admob.api.AdMobApi
import com.oztechan.adtrack.data.admob.api.AdMobApiImpl
import com.oztechan.adtrack.domain.PeriodCalculator
import com.oztechan.adtrack.domain.repository.RevenueRepository
import com.oztechan.adtrack.domain.repository.RevenueRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val adMobModule = module {
    single<AdMobApi> { AdMobApiImpl(get(AUTH_CLIENT)) }
    single { PeriodCalculator() }
    singleOf(::RevenueRepositoryImpl) bind RevenueRepository::class
}
