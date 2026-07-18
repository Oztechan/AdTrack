/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

// One snapshot test per component/state keeps failures pinpointed; grouping them here is the point.
@file:Suppress("TooManyFunctions")

package com.oztechan.adtrack.screenshot

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.github.takahirom.roborazzi.captureRoboImage
import com.oztechan.adtrack.domain.model.AdFormat
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.FormatRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import com.oztechan.adtrack.ui.components.AppRevenueRow
import com.oztechan.adtrack.ui.components.FormatRevenueRow
import com.oztechan.adtrack.ui.components.PeriodSelector
import com.oztechan.adtrack.ui.components.RevenueChart
import com.oztechan.adtrack.ui.components.SummaryCard
import com.oztechan.adtrack.ui.feature.appdetail.AppDetailScreenContent
import com.oztechan.adtrack.ui.feature.appdetail.AppDetailState
import com.oztechan.adtrack.ui.feature.dashboard.DashboardScreenContent
import com.oztechan.adtrack.ui.feature.dashboard.DashboardState
import com.oztechan.adtrack.ui.theme.AdTrackTheme
import kotlinx.datetime.LocalDate
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private const val SNAPSHOT_PHONE = "w411dp-h914dp-normal-long-notround-any-420dpi-keyshidden-nonav"
private const val SNAPSHOT_DIR = "src/androidUnitTest/snapshots"

/**
 * Golden-image regression suite: every component/state is snapshotted in light and dark theme on
 * one phone config. `verifyRoborazziDebug` fails a PR whose UI pixels drift from the committed
 * goldens; after an intended change run `recordRoborazziDebug` and commit the updated images.
 * (Store-listing screenshots live separately in `StoreScreenshots` / `art/store`.)
 */
@Suppress("MagicNumber") // representative sample data
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = SNAPSHOT_PHONE)
class SnapshotTests {

    @Test
    fun summaryCard() = snapshot("summary_card") {
        SummaryCard(RevenueSummary(Period.LAST_30_DAYS, "USD", 128.42, 15_420, 312, 101.10, 27.0))
    }

    @Test
    fun summaryCardNegativeDelta() = snapshot("summary_card_negative_delta") {
        SummaryCard(RevenueSummary(Period.TODAY, "USD", 3.12, 2_410, 44, 4.20, -25.7))
    }

    @Test
    fun appRevenueRow() = snapshot("app_revenue_row") {
        AppRevenueRow(AppRevenue("app1", "Currency Converter", 84.10, 9_000, 180), "USD", onClick = {})
    }

    @Test
    fun formatRevenueRow() = snapshot("format_revenue_row") {
        FormatRevenueRow(FormatRevenue(AdFormat.REWARDED, "Rewarded", 52.40, 3_200, 96), "USD")
    }

    @Test
    fun revenueChartBar() = snapshot("revenue_chart_bar") {
        RevenueChart(points = sampleSeries(), currencyCode = "USD")
    }

    @Test
    fun periodSelector() = snapshot("period_selector") {
        PeriodSelector(selected = Period.LAST_30_DAYS, onSelected = {})
    }

    @Test
    fun dashboardLoading() = snapshot("dashboard_loading") {
        DashboardScreenContent(
            state = DashboardState(isLoading = true),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }

    @Test
    fun dashboardError() = snapshot("dashboard_error") {
        DashboardScreenContent(
            state = DashboardState(errorMessage = "No AdMob account is linked to this Google account."),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }

    @Test
    fun dashboardEmptyApps() = snapshot("dashboard_empty_apps") {
        DashboardScreenContent(
            state = DashboardState(
                selectedPeriod = Period.LAST_30_DAYS,
                summary = RevenueSummary(Period.LAST_30_DAYS, "USD", 0.0, 0, 0, null, null)
            ),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }

    @Test
    fun appDetailNoData() = snapshot("app_detail_no_data") {
        AppDetailScreenContent(
            state = AppDetailState(
                appName = "Currency Converter",
                period = Period.TODAY,
                isLoading = false,
                app = null
            ),
            onBackClick = {}
        )
    }

    private fun sampleSeries() = (1..14).map { day ->
        RevenuePoint(LocalDate(2026, 6, day), earnings = 6.0 + (day % 5) * 2.1)
    }

    // Captures the content twice — once per theme — on an app-background surface so components
    // are readable against the correct backdrop in both variants.
    private fun snapshot(name: String, content: @Composable () -> Unit) {
        listOf(false to "light", true to "dark").forEach { (dark, suffix) ->
            captureRoboImage(filePath = "$SNAPSHOT_DIR/${name}_$suffix.png") {
                AdTrackTheme(darkTheme = dark) {
                    Surface(color = MaterialTheme.colorScheme.background) { content() }
                }
            }
        }
    }
}
