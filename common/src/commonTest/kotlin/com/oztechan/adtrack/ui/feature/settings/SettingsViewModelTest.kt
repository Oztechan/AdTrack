/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.settings

import com.oztechan.adtrack.ads.premium.PremiumManagerImpl
import com.oztechan.adtrack.ads.rewarded.PlatformRewardedAd
import com.oztechan.adtrack.ads.rewarded.RewardedAdManager
import com.oztechan.adtrack.core.storage.SecureStorage
import com.oztechan.adtrack.domain.repository.AuthRepository
import com.oztechan.adtrack.domain.repository.RevenueRepository
import com.oztechan.adtrack.fakes.FakeAuthRepository
import com.oztechan.adtrack.fakes.FakeRevenueRepository
import com.oztechan.adtrack.fakes.fakeAccount
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakePlatformRewardedAd(private val rewardedAdManager: RewardedAdManager) : PlatformRewardedAd {
    var showCount = 0
    override fun show() {
        showCount++
    }

    fun completeWithReward() = rewardedAdManager.onRewardEarned()
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    // The VM and the RewardedAdManager must share one PremiumManager so a reward flips the VM's state.
    private val premiumManager = PremiumManagerImpl(SecureStorage(MapSettings()))
    private val rewardedAdManager = RewardedAdManager(premiumManager)
    private val platformRewardedAd = FakePlatformRewardedAd(rewardedAdManager)

    private fun viewModel(
        auth: AuthRepository = FakeAuthRepository(),
        revenue: RevenueRepository = FakeRevenueRepository()
    ) = SettingsViewModel(
        authRepository = auth,
        revenueRepository = revenue,
        premiumManager = premiumManager,
        rewardedAdManager = rewardedAdManager,
        platformRewardedAd = platformRewardedAd
    )

    @Test
    fun load_populates_account_info() = runTest(dispatcher) {
        val repository = FakeRevenueRepository(account = fakeAccount(publisherId = "pub-x", currencyCode = "GBP"))
        val viewModel = viewModel(revenue = repository)
        advanceUntilIdle()

        assertEquals("pub-x", viewModel.state.value.publisherId)
        assertEquals("GBP", viewModel.state.value.currencyCode)
    }

    @Test
    fun sign_out_clears_session_and_navigates() = runTest(dispatcher) {
        val auth = FakeAuthRepository(signedIn = true)
        val revenue = FakeRevenueRepository()
        val viewModel = viewModel(auth = auth, revenue = revenue)
        advanceUntilIdle()
        val effects = mutableListOf<SettingsEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.collect(effects::add) }

        viewModel.event.onSignOutClick()
        advanceUntilIdle()

        assertTrue(auth.signOutCalled)
        assertTrue(revenue.invalidateCalled)
        assertTrue(effects.contains(SettingsEffect.NavigateToSignIn))
    }

    @Test
    fun watching_rewarded_ad_shows_it_then_reward_flips_premium() = runTest(dispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.isPremium)

        viewModel.event.onWatchRewardedAd()
        assertEquals(1, platformRewardedAd.showCount)

        platformRewardedAd.completeWithReward()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isPremium)
    }
}
