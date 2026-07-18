/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.appdetail

import androidx.lifecycle.viewModelScope
import com.oztechan.adtrack.core.viewmodel.SEEDViewModel
import com.oztechan.adtrack.data.admob.model.AdMobAccount
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.FormatRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.repository.RevenueRepository
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
                    val formats = async { revenueRepository.getFormatBreakdown(period, appId) }
                    LoadResult(account.await(), apps.await(), series.await(), formats.await())
                }
            }.onSuccess { result ->
                setState {
                    copy(
                        isLoading = false,
                        app = result.apps.firstOrNull { it.appId == appId },
                        series = result.series,
                        formats = result.formats,
                        currencyCode = result.account.currencyCode
                    )
                }
            }.onFailure { error ->
                setState { copy(isLoading = false, errorMessage = error.message ?: "Failed to load") }
            }
        }
    }

    private data class LoadResult(
        val account: AdMobAccount,
        val apps: List<AppRevenue>,
        val series: List<RevenuePoint>,
        val formats: List<FormatRevenue>
    )
}
