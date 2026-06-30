/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.dashboard

import androidx.lifecycle.viewModelScope
import com.oztechan.adtrace.core.viewmodel.SEEDViewModel
import com.oztechan.adtrace.domain.model.AppRevenue
import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.domain.repository.RevenueRepository
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

    private fun load(period: Period, refresh: Boolean) {
        viewModelScope.launch {
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
                    val apps = async { revenueRepository.getAppBreakdown(period) }
                    val series = async { revenueRepository.getRevenueSeries(period) }
                    Triple(summary.await(), apps.await(), series.await())
                }
            }.onSuccess { (summary, apps, series) ->
                setState {
                    copy(
                        isLoading = false,
                        isRefreshing = false,
                        summary = summary,
                        apps = apps,
                        series = series
                    )
                }
            }.onFailure { error ->
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
}
