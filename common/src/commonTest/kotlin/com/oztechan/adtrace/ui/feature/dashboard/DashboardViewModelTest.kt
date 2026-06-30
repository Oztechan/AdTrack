/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.dashboard

import com.oztechan.adtrace.domain.model.AppRevenue
import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.fakes.FakeRevenueRepository
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun initial_load_populates_summary_and_apps() = runTest(dispatcher) {
        val viewModel = DashboardViewModel(FakeRevenueRepository())
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(12.5, state.summary?.earnings)
        assertTrue(state.apps.isNotEmpty())
    }

    @Test
    fun load_failure_sets_error_message() = runTest(dispatcher) {
        val viewModel = DashboardViewModel(FakeRevenueRepository(error = RuntimeException("network down")))
        advanceUntilIdle()

        assertEquals("network down", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun selecting_a_period_updates_state() = runTest(dispatcher) {
        val viewModel = DashboardViewModel(FakeRevenueRepository())
        advanceUntilIdle()

        viewModel.event.onPeriodSelected(Period.LAST_30_DAYS)
        advanceUntilIdle()

        assertEquals(Period.LAST_30_DAYS, viewModel.state.value.selectedPeriod)
    }

    @Test
    fun refresh_invalidates_the_repository_cache() = runTest(dispatcher) {
        val repository = FakeRevenueRepository()
        val viewModel = DashboardViewModel(repository)
        advanceUntilIdle()

        viewModel.event.onRefresh()
        advanceUntilIdle()

        assertTrue(repository.invalidateCalled)
    }

    @Test
    fun app_click_emits_navigation_effect() = runTest(dispatcher) {
        val viewModel = DashboardViewModel(FakeRevenueRepository())
        advanceUntilIdle()
        val effects = mutableListOf<DashboardEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.collect(effects::add) }

        viewModel.event.onAppClick(AppRevenue("app1", "App One", 9.0, 80, 4))
        advanceUntilIdle()

        val effect = effects.filterIsInstance<DashboardEffect.NavigateToAppDetail>().single()
        assertEquals("app1", effect.appId)
    }

    @Test
    fun settings_click_emits_navigation_effect() = runTest(dispatcher) {
        val viewModel = DashboardViewModel(FakeRevenueRepository())
        advanceUntilIdle()
        val effects = mutableListOf<DashboardEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.collect(effects::add) }

        viewModel.event.onSettingsClick()
        advanceUntilIdle()

        assertEquals(DashboardEffect.NavigateToSettings, effects.single())
    }
}
