/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.appdetail

import androidx.lifecycle.viewModelScope
import com.oztechan.adtrace.core.viewmodel.SEEDViewModel
import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.domain.repository.RevenueRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AppDetailViewModel(
    private val revenueRepository: RevenueRepository,
    private val appId: String,
    appName: String,
    private val period: Period
) : SEEDViewModel<AppDetailState, AppDetailEffect, AppDetailEvent, AppDetailData>(
    initialState = AppDetailState(appName = appName, period = period),
    initialData = AppDetailData()
),
    AppDetailEvent {

    init {
        load()
    }

    override fun onRetry() = load()

    override fun onBackClick() = sendEffect { AppDetailEffect.NavigateBack }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            runCatching {
                coroutineScope {
                    val account = async { revenueRepository.getAccount() }
                    val apps = async { revenueRepository.getAppBreakdown(period) }
                    val series = async { revenueRepository.getAppRevenueSeries(period, appId) }
                    Triple(account.await(), apps.await(), series.await())
                }
            }.onSuccess { (account, apps, series) ->
                setState {
                    copy(
                        isLoading = false,
                        app = apps.firstOrNull { it.appId == appId },
                        series = series,
                        currencyCode = account.currencyCode
                    )
                }
            }.onFailure { error ->
                setState { copy(isLoading = false, errorMessage = error.message ?: "Failed to load") }
            }
        }
    }
}
