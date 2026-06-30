/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.di

import com.oztechan.adtrace.core.network.di.networkModule
import com.oztechan.adtrace.data.admob.di.adMobModule
import com.oztechan.adtrace.data.auth.di.authModule
import com.oztechan.adtrace.ui.di.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        platformModule,
        networkModule,
        authModule,
        adMobModule,
        viewModelModule
    )
}
