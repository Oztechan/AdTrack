/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.settings

import com.oztechan.adtrace.fakes.FakeAuthRepository
import com.oztechan.adtrace.fakes.FakeRevenueRepository
import com.oztechan.adtrace.fakes.fakeAccount
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun load_populates_account_info() = runTest(dispatcher) {
        val repository = FakeRevenueRepository(account = fakeAccount(publisherId = "pub-x", currencyCode = "GBP"))
        val viewModel = SettingsViewModel(FakeAuthRepository(), repository)
        advanceUntilIdle()

        assertEquals("pub-x", viewModel.state.value.publisherId)
        assertEquals("GBP", viewModel.state.value.currencyCode)
    }

    @Test
    fun sign_out_clears_session_and_navigates() = runTest(dispatcher) {
        val auth = FakeAuthRepository(signedIn = true)
        val revenue = FakeRevenueRepository()
        val viewModel = SettingsViewModel(auth, revenue)
        advanceUntilIdle()
        val effects = mutableListOf<SettingsEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.collect(effects::add) }

        viewModel.event.onSignOutClick()
        advanceUntilIdle()

        assertTrue(auth.signOutCalled)
        assertTrue(revenue.invalidateCalled)
        assertTrue(effects.contains(SettingsEffect.NavigateToSignIn))
    }
}
