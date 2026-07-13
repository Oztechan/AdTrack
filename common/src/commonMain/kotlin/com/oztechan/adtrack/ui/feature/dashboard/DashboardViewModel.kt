/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.dashboard

import androidx.lifecycle.viewModelScope
import com.oztechan.adtrack.core.viewmodel.SEEDViewModel
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import com.oztechan.adtrack.domain.repository.RevenueRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val revenueRepository: RevenueRepository
) : SEEDViewModel<DashboardState, DashboardEffect, DashboardEvent, DashboardData>(
    initialState = DashboardState(),
    initialData = DashboardData()
),
    DashboardEvent {

    private var loadJob: Job? = null

    init {
        load(state.value.selectedPeriod, refresh = false)
    }

    override fun onPeriodSelected(period: Period) {
        if (period == state.value.selectedPeriod && state.value.summary != null) return
        setState { copy(selectedPeriod = period) }
        load(period, refresh = false)
    }

    override fun onRefresh() {
        revenueRepository.invalidate()
        load(state.value.selectedPeriod, refresh = true)
    }

    override fun onRetry() = load(state.value.selectedPeriod, refresh = false)

    override fun onAppClick(app: AppRevenue) = sendEffect {
        DashboardEffect.NavigateToAppDetail(app.appId, app.appName, state.value.selectedPeriod)
    }

    override fun onSettingsClick() = sendEffect { DashboardEffect.NavigateToSettings }

    // Cancels the in-flight load: without this, switching periods quickly lets the slower older
    // request finish last and overwrite the state of the newer selection.
    private fun load(period: Period, refresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            setState {
                copy(
                    isLoading = !refresh,
                    isRefreshing = refresh,
                    errorMessage = null
                )
            }
            runCatching {
                coroutineScope {
                    val summary = async { revenueRepository.getSummary(period) }
                    // TODAY has no chart (single data point), so yesterday's card fills the gap.
                    val yesterday = async {
                        if (period == Period.TODAY) revenueRepository.getYesterdaySummary() else null
                    }
                    val apps = async { revenueRepository.getAppBreakdown(period) }
                    val series = async { revenueRepository.getRevenueSeries(period) }
                    Loaded(summary.await(), yesterday.await(), apps.await(), series.await())
                }
            }.onSuccess { loaded ->
                setState {
                    copy(
                        isLoading = false,
                        isRefreshing = false,
                        summary = loaded.summary,
                        yesterdaySummary = loaded.yesterdaySummary,
                        apps = loaded.apps,
                        series = loaded.series
                    )
                }
            }.onFailure { error ->
                // Rethrow so a cancelled load (superseded by a newer selection) never
                // paints an error over the state the new load is about to produce.
                if (error is CancellationException) throw error
                setState {
                    copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = error.message ?: "Failed to load revenue"
                    )
                }
            }
        }
    }

    private data class Loaded(
        val summary: RevenueSummary,
        val yesterdaySummary: RevenueSummary?,
        val apps: List<AppRevenue>,
        val series: List<RevenuePoint>
    )
}
