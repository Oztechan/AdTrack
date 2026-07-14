/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

// Groups the period/platform label helpers with the revenue display composables and their @Previews.
@file:Suppress("TooManyFunctions")

package com.oztechan.adtrack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oztechan.adtrack.core.util.formatCurrency
import com.oztechan.adtrack.domain.model.AppPlatform
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenueSummary
import com.oztechan.adtrack.ui.theme.AdTrackTheme
import com.oztechan.adtrack.ui.theme.LocalAdTrackColors
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs

fun Period.label(): String = when (this) {
    Period.TODAY -> "Today"
    Period.LAST_30_DAYS -> "Last 30 Days"
    Period.LAST_90_DAYS -> "Last 90 Days"
    Period.LAST_365_DAYS -> "Last 365 Days"
    Period.LIFETIME -> "Lifetime"
}

fun AppPlatform.label(): String? = when (this) {
    AppPlatform.ANDROID -> "Android"
    AppPlatform.IOS -> "iOS"
    AppPlatform.UNKNOWN -> null
}

// The TODAY view also shows yesterday's card, so its selector chip names both days. Card titles
// and other texts keep the plain [label] since they refer to a single day's data.
private fun Period.selectorLabel(): String =
    if (this == Period.TODAY) "Today/Yesterday" else label()

@Composable
fun PeriodSelector(
    selected: Period,
    onSelected: (Period) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Period.entries.forEach { period ->
            FilterChip(
                selected = period == selected,
                onClick = { onSelected(period) },
                label = { Text(period.selectorLabel()) }
            )
        }
    }
}

@Composable
fun SummaryCard(
    summary: RevenueSummary,
    modifier: Modifier = Modifier,
    title: String = summary.period.label()
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = formatCurrency(summary.earnings, summary.currencyCode),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            summary.deltaPercent?.let { delta -> DeltaLabel(delta) }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("Impressions", summary.impressions.toString())
                MetricColumn("Clicks", summary.clicks.toString())
            }
        }
    }
}

@Composable
private fun DeltaLabel(delta: Double) {
    val up = delta >= 0
    val colors = LocalAdTrackColors.current
    val color = if (up) colors.positive else colors.negative
    val arrow = if (up) "▲" else "▼"
    val rounded = (abs(delta) * 10).toLong() / 10.0
    Text(
        text = "$arrow $rounded% vs previous",
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun MetricColumn(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AppRevenueRow(
    app: AppRevenue,
    currencyCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text(
                text = app.appName.ifBlank { "Unknown app" },
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = listOfNotNull(
                    app.platform.label(),
                    "${app.impressions} impressions",
                    "${app.clicks} clicks"
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = formatCurrency(app.earnings, currencyCode),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
private fun SummaryCardPreview() {
    AdTrackTheme {
        SummaryCard(RevenueSummary(Period.LAST_30_DAYS, "USD", 128.42, 15_420, 312, 101.1, 27.0))
    }
}

@Preview
@Composable
private fun AppRevenueRowPreview() {
    AdTrackTheme {
        AppRevenueRow(AppRevenue("a", "Currency Converter", 84.10, 9000, 180, AppPlatform.ANDROID), "USD", {})
    }
}

@Preview
@Composable
private fun PeriodSelectorPreview() {
    AdTrackTheme {
        PeriodSelector(selected = Period.LAST_30_DAYS, onSelected = {})
    }
}
