/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.di

import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.ui.feature.appdetail.AppDetailViewModel
import com.oztechan.adtrace.ui.feature.dashboard.DashboardViewModel
import com.oztechan.adtrace.ui.feature.settings.SettingsViewModel
import com.oztechan.adtrace.ui.feature.signin.SignInViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::SettingsViewModel)
    viewModel { (appId: String, appName: String, period: Period) ->
        AppDetailViewModel(get(), appId, appName, period)
    }
}
