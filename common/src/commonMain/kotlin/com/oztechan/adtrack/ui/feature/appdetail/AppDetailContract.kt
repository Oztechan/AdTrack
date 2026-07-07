/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.appdetail

import com.oztechan.adtrack.core.viewmodel.BaseData
import com.oztechan.adtrack.core.viewmodel.BaseEffect
import com.oztechan.adtrack.core.viewmodel.BaseEvent
import com.oztechan.adtrack.core.viewmodel.BaseState
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint

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
