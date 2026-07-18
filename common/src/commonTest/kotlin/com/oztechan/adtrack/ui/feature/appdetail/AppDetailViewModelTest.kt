/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.appdetail

import com.oztechan.adtrack.domain.model.AdFormat
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.FormatRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.fakes.FakeRevenueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AppDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun repository() = FakeRevenueRepository(
        apps = listOf(AppRevenue("app1", "App One", 9.0, 80, 4)),
        series = listOf(
            RevenuePoint(LocalDate(2026, 1, 1), 1.0),
            RevenuePoint(LocalDate(2026, 1, 2), 2.0)
        ),
        formats = listOf(
            FormatRevenue(AdFormat.REWARDED, "Rewarded", 6.0, 50, 3),
            FormatRevenue(AdFormat.BANNER, "Banner", 3.0, 30, 1)
        )
    )

    @Test
    fun load_resolves_the_app_its_series_and_format_breakdown() = runTest(dispatcher) {
        val repository = repository()
        val viewModel = AppDetailViewModel(
            repository,
            appId = "app1",
            appName = "App One",
            period = Period.LAST_30_DAYS
        )
        advanceUntilIdle()

        assertEquals("app1", viewModel.state.value.app?.appId)
        assertEquals(2, viewModel.state.value.series.size)
        assertEquals("app1", repository.lastAppSeriesId)
        assertEquals(2, viewModel.state.value.formats.size)
        assertEquals(AdFormat.REWARDED, viewModel.state.value.formats.first().format)
        assertEquals("app1", repository.lastFormatBreakdownId)
    }

    @Test
    fun back_click_emits_navigate_back() = runTest(dispatcher) {
        val viewModel = AppDetailViewModel(repository(), appId = "app1", appName = "App One", period = Period.TODAY)
        advanceUntilIdle()
        val effects = mutableListOf<AppDetailEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.collect(effects::add) }

        viewModel.event.onBackClick()
        advanceUntilIdle()

        assertEquals(AppDetailEffect.NavigateBack, effects.single())
    }
}
