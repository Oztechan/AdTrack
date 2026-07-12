/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.dashboard

import com.oztechan.adtrack.core.viewmodel.BaseData
import com.oztechan.adtrack.core.viewmodel.BaseEffect
import com.oztechan.adtrack.core.viewmodel.BaseEvent
import com.oztechan.adtrack.core.viewmodel.BaseState
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary

data class DashboardState(
    val selectedPeriod: Period = Period.TODAY,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val summary: RevenueSummary? = null,
    val yesterdaySummary: RevenueSummary? = null,
    val apps: List<AppRevenue> = emptyList(),
    val series: List<RevenuePoint> = emptyList(),
    val errorMessage: String? = null
) : BaseState

sealed interface DashboardEffect : BaseEffect {
    data class NavigateToAppDetail(
        val appId: String,
        val appName: String,
        val period: Period
    ) : DashboardEffect

    data object NavigateToSettings : DashboardEffect
}

interface DashboardEvent : BaseEvent {
    fun onPeriodSelected(period: Period)
    fun onRefresh()
    fun onRetry()
    fun onAppClick(app: AppRevenue)
    fun onSettingsClick()
}

class DashboardData : BaseData
