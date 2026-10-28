/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.demo.di

import com.oztechan.adtrack.demo.DemoManager
import com.oztechan.adtrack.demo.DemoManagerImpl
import com.oztechan.adtrack.demo.DemoRevenueRepository
import com.oztechan.adtrack.domain.repository.RevenueRepository
import com.oztechan.adtrack.domain.repository.RevenueRepositoryImpl
import org.koin.dsl.module

val demoModule = module {
    single<DemoManager> { DemoManagerImpl() }
    // The app-wide RevenueRepository is the demo overlay; it delegates to the real
    // RevenueRepositoryImpl (provided concretely by adMobModule) when not in demo mode.
    single<RevenueRepository> { DemoRevenueRepository(get(), get<RevenueRepositoryImpl>()) }
}
