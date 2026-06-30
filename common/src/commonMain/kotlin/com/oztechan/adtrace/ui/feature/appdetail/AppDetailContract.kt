/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.appdetail

import com.oztechan.adtrace.core.viewmodel.BaseData
import com.oztechan.adtrace.core.viewmodel.BaseEffect
import com.oztechan.adtrace.core.viewmodel.BaseEvent
import com.oztechan.adtrace.core.viewmodel.BaseState
import com.oztechan.adtrace.domain.model.AppRevenue
import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.domain.model.RevenuePoint

data class AppDetailState(
    val appName: String = "",
    val period: Period = Period.TODAY,
    val isLoading: Boolean = true,
    val app: AppRevenue? = null,
    val series: List<RevenuePoint> = emptyList(),
    val currencyCode: String = "USD",
    val errorMessage: String? = null
) : BaseState

sealed interface AppDetailEffect : BaseEffect {
    data object NavigateBack : AppDetailEffect
}

interface AppDetailEvent : BaseEvent {
    fun onRetry()
    fun onBackClick()
}

class AppDetailData : BaseData
