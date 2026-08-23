/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.di

import android.app.Application
import com.oztechan.adtrack.ads.premium.di.premiumModule
import com.oztechan.adtrack.ads.rewarded.PlatformRewardedAd
import com.oztechan.adtrack.ads.rewarded.di.rewardedModule
import com.oztechan.adtrack.core.network.di.networkModule
import com.oztechan.adtrack.data.admob.di.adMobModule
import com.oztechan.adtrack.data.auth.di.authModule
import com.oztechan.adtrack.di.testPlatformDeps
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.ui.feature.appdetail.AppDetailViewModel
import com.oztechan.adtrack.ui.feature.dashboard.DashboardViewModel
import com.oztechan.adtrack.ui.feature.settings.SettingsViewModel
import com.oztechan.adtrack.ui.feature.signin.SignInViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

// PlatformRewardedAd is a platform seam (Android impl lives in androidApp); fake it for the graph test.
private class FakePlatformRewardedAd : PlatformRewardedAd {
    override fun show() = Unit
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class ViewModelModuleTest {

    // The view models launch work on the main dispatcher in init; provide a test one.
    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun provides_all_view_models() {
        val koin = startKoin {
            modules(
                viewModelModule,
                adMobModule,
                authModule,
                networkModule,
                premiumModule,
                rewardedModule,
                testPlatformDeps(),
                module { single<PlatformRewardedAd> { FakePlatformRewardedAd() } }
            )
        }.koin

        assertNotNull(koin.getOrNull<SignInViewModel>())
        assertNotNull(koin.getOrNull<DashboardViewModel>())
        assertNotNull(koin.getOrNull<SettingsViewModel>())
        assertNotNull(koin.get<AppDetailViewModel> { parametersOf("appId", "App", Period.TODAY) })
    }
}
