/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.di

import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.ui.feature.appdetail.AppDetailViewModel
import com.oztechan.adtrack.ui.feature.dashboard.DashboardViewModel
import com.oztechan.adtrack.ui.feature.settings.SettingsViewModel
import com.oztechan.adtrack.ui.feature.signin.SignInViewModel
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
