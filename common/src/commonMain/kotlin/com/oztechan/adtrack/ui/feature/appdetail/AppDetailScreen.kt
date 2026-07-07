/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.appdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oztechan.adtrack.core.util.formatCurrency
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import com.oztechan.adtrack.ui.components.RevenueChart
import com.oztechan.adtrack.ui.components.SummaryCard
import com.oztechan.adtrack.ui.components.label
import com.oztechan.adtrack.ui.theme.AdTrackTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppDetailScreen(
    appId: String,
    appName: String,
    period: Period,
    onNavigateBack: () -> Unit,
    viewModel: AppDetailViewModel = koinViewModel { parametersOf(appId, appName, period) }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AppDetailEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    AppDetailScreenContent(state = state, onBackClick = viewModel.event::onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDetailScreenContent(
    state: AppDetailState,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.appName.ifBlank { "App" }) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (state.isLoading) CenteredProgress() else AppDetailContent(state)
        }
    }
}

@Composable
private fun AppDetailContent(state: AppDetailState) {
    val app = state.app
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (app != null) {
            item {
                SummaryCard(
                    RevenueSummary(
                        period = state.period,
                        currencyCode = state.currencyCode,
                        earnings = app.earnings,
                        impressions = app.impressions,
                        clicks = app.clicks,
                        previousEarnings = null,
                        deltaPercent = null
                    )
                )
            }
        } else {
            item { Text("No data for this app in ${state.period.label()}.") }
        }

        if (state.series.size >= 2) {
            item {
                RevenueChart(
                    points = state.series,
                    currencyCode = state.currencyCode,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        item {
            Text(
                text = "Daily earnings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        items(state.series, key = { it.date.toString() }) { point ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(point.date.toString(), style = MaterialTheme.typography.bodyMedium)
                Text(formatCurrency(point.earnings, state.currencyCode), style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun CenteredProgress() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun AppDetailLoadedPreview() {
    AdTrackTheme {
        AppDetailScreenContent(
            state = AppDetailState(
                appName = "Currency Converter",
                period = Period.LAST_30_DAYS,
                isLoading = false,
                app = AppRevenue("app1", "Currency Converter", 84.10, 9000, 180),
                series = (1..7).map { RevenuePoint(LocalDate(2026, 1, it), 8.0 + it * 2.0) },
                currencyCode = "USD"
            ),
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun AppDetailLoadingPreview() {
    AdTrackTheme {
        AppDetailScreenContent(
            state = AppDetailState(appName = "Currency Converter", period = Period.TODAY, isLoading = true),
            onBackClick = {}
        )
    }
}
