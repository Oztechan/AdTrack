/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oztechan.adtrace.domain.model.AppRevenue
import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.domain.model.RevenuePoint
import com.oztechan.adtrace.domain.model.RevenueSummary
import com.oztechan.adtrace.ui.components.AppRevenueRow
import com.oztechan.adtrace.ui.components.PeriodSelector
import com.oztechan.adtrace.ui.components.RevenueChart
import com.oztechan.adtrace.ui.components.SummaryCard
import com.oztechan.adtrace.ui.theme.AdTraceTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    onNavigateToAppDetail: (appId: String, appName: String, period: Period) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is DashboardEffect.NavigateToAppDetail ->
                    onNavigateToAppDetail(effect.appId, effect.appName, effect.period)
                DashboardEffect.NavigateToSettings -> onNavigateToSettings()
            }
        }
    }

    DashboardScreenContent(
        state = state,
        onPeriodSelected = viewModel.event::onPeriodSelected,
        onRefresh = viewModel.event::onRefresh,
        onRetry = viewModel.event::onRetry,
        onAppClick = viewModel.event::onAppClick,
        onSettingsClick = viewModel.event::onSettingsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenContent(
    state: DashboardState,
    onPeriodSelected: (Period) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onAppClick: (AppRevenue) -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AdTrace") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            PeriodSelector(selected = state.selectedPeriod, onSelected = onPeriodSelected)

            when {
                state.isLoading -> CenteredProgress()
                state.errorMessage != null -> ErrorState(requireNotNull(state.errorMessage), onRetry)
                else -> DashboardContent(state, onAppClick)
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardState,
    onAppClick: (AppRevenue) -> Unit
) {
    val currency = state.summary?.currencyCode ?: "USD"
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.summary?.let { summary ->
            item { SummaryCard(summary, modifier = Modifier.padding(top = 16.dp)) }
        }
        if (state.series.size >= 2) {
            item {
                RevenueChart(
                    points = state.series,
                    currencyCode = currency,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        item {
            Text(
                text = "Apps",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
        }
        if (state.apps.isEmpty()) {
            item { Text("No app revenue for this period.", style = MaterialTheme.typography.bodyMedium) }
        }
        items(state.apps, key = { it.appId.ifBlank { it.appName } }) { app ->
            AppRevenueRow(app = app, currencyCode = currency, onClick = { onAppClick(app) })
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

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}

// region Previews
private fun sampleSummary() = RevenueSummary(
    period = Period.LAST_7_DAYS,
    currencyCode = "USD",
    earnings = 128.42,
    impressions = 15_420,
    clicks = 312,
    previousEarnings = 101.10,
    deltaPercent = 27.0
)

private fun sampleApps() = listOf(
    AppRevenue("app1", "Currency Converter", 84.10, 9000, 180),
    AppRevenue("app2", "TraceFit", 44.32, 6420, 132)
)

private fun sampleSeries() = (1..7).map { day ->
    RevenuePoint(LocalDate(2026, 1, day), earnings = 8.0 + day * 2.5)
}

@Preview
@Composable
private fun DashboardLoadedPreview() {
    AdTraceTheme {
        DashboardScreenContent(
            state = DashboardState(
                selectedPeriod = Period.LAST_7_DAYS,
                summary = sampleSummary(),
                apps = sampleApps(),
                series = sampleSeries()
            ),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }
}

@Preview
@Composable
private fun DashboardLoadingPreview() {
    AdTraceTheme {
        DashboardScreenContent(
            state = DashboardState(isLoading = true),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }
}

@Preview
@Composable
private fun DashboardErrorPreview() {
    AdTraceTheme {
        DashboardScreenContent(
            state = DashboardState(errorMessage = "No AdMob account is linked to this Google account."),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }
}
// endregion
