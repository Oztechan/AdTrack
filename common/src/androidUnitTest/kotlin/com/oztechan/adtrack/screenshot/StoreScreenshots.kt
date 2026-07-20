/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.screenshot

import androidx.compose.runtime.Composable
import com.github.takahirom.roborazzi.captureRoboImage
import com.oztechan.adtrack.domain.model.AdFormat
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.FormatRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import com.oztechan.adtrack.ui.feature.appdetail.AppDetailScreenContent
import com.oztechan.adtrack.ui.feature.appdetail.AppDetailState
import com.oztechan.adtrack.ui.feature.dashboard.DashboardScreenContent
import com.oztechan.adtrack.ui.feature.dashboard.DashboardState
import com.oztechan.adtrack.ui.theme.AdTrackTheme
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// Store device classes. dp size × dpi = pixel output.
// Google Play:
private const val PHONE = "w411dp-h914dp-normal-long-notround-any-420dpi-keyshidden-nonav" // 1080x2400
private const val TABLET_7IN = "w600dp-h960dp-large-long-notround-any-xhdpi-keyshidden-nonav" // 1200x1920
private const val TABLET_10IN = "w800dp-h1280dp-xlarge-long-notround-any-xhdpi-keyshidden-nonav" // 1600x2560

// App Store (same Compose UI ships on iOS):
private const val IPHONE_65IN = "w414dp-h896dp-normal-long-notround-any-xxhdpi-keyshidden-nonav" // 1242x2688
private const val IPAD_13IN = "w1032dp-h1376dp-xlarge-notlong-notround-any-xhdpi-keyshidden-nonav" // 2064x2752

/**
 * Renders the shared screens with curated sample data into PNGs for the store listings —
 * no emulator, no sign-in, no real revenue numbers. The images are committed under
 * `art/store/screenshots/` so they double as golden references: `verifyRoborazziDebug` fails
 * when a change alters them, signalling the listings are stale. Regenerate with
 * `./gradlew :common:recordRoborazziDebug`.
 */
@Suppress("MagicNumber") // curated store-listing sample data
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = PHONE)
class StoreScreenshots {

    @Test
    fun dashboardTodayPhone() = captureDashboardToday("phone")

    @Test
    @Config(qualifiers = TABLET_7IN)
    fun dashboardTodayTablet7in() = captureDashboardToday("tablet-7in")

    @Test
    @Config(qualifiers = TABLET_10IN)
    fun dashboardTodayTablet10in() = captureDashboardToday("tablet-10in")

    @Test
    @Config(qualifiers = IPHONE_65IN)
    fun dashboardTodayIphone65in() = captureDashboardToday("iphone-6.5in")

    @Test
    @Config(qualifiers = IPAD_13IN)
    fun dashboardTodayIpad13in() = captureDashboardToday("ipad-13in")

    @Test
    fun dashboardMonthPhone() = captureDashboardMonth("phone")

    @Test
    @Config(qualifiers = TABLET_7IN)
    fun dashboardMonthTablet7in() = captureDashboardMonth("tablet-7in")

    @Test
    @Config(qualifiers = TABLET_10IN)
    fun dashboardMonthTablet10in() = captureDashboardMonth("tablet-10in")

    @Test
    @Config(qualifiers = IPHONE_65IN)
    fun dashboardMonthIphone65in() = captureDashboardMonth("iphone-6.5in")

    @Test
    @Config(qualifiers = IPAD_13IN)
    fun dashboardMonthIpad13in() = captureDashboardMonth("ipad-13in")

    @Test
    fun dashboardYearPhone() = captureDashboardYear("phone")

    @Test
    @Config(qualifiers = TABLET_7IN)
    fun dashboardYearTablet7in() = captureDashboardYear("tablet-7in")

    @Test
    @Config(qualifiers = TABLET_10IN)
    fun dashboardYearTablet10in() = captureDashboardYear("tablet-10in")

    @Test
    @Config(qualifiers = IPHONE_65IN)
    fun dashboardYearIphone65in() = captureDashboardYear("iphone-6.5in")

    @Test
    @Config(qualifiers = IPAD_13IN)
    fun dashboardYearIpad13in() = captureDashboardYear("ipad-13in")

    @Test
    fun dashboardLifetimePhone() = captureDashboardLifetime("phone")

    @Test
    @Config(qualifiers = TABLET_7IN)
    fun dashboardLifetimeTablet7in() = captureDashboardLifetime("tablet-7in")

    @Test
    @Config(qualifiers = TABLET_10IN)
    fun dashboardLifetimeTablet10in() = captureDashboardLifetime("tablet-10in")

    @Test
    @Config(qualifiers = IPHONE_65IN)
    fun dashboardLifetimeIphone65in() = captureDashboardLifetime("iphone-6.5in")

    @Test
    @Config(qualifiers = IPAD_13IN)
    fun dashboardLifetimeIpad13in() = captureDashboardLifetime("ipad-13in")

    @Test
    fun appDetailPhone() = captureAppDetail("phone")

    @Test
    @Config(qualifiers = TABLET_7IN)
    fun appDetailTablet7in() = captureAppDetail("tablet-7in")

    @Test
    @Config(qualifiers = TABLET_10IN)
    fun appDetailTablet10in() = captureAppDetail("tablet-10in")

    @Test
    @Config(qualifiers = IPHONE_65IN)
    fun appDetailIphone65in() = captureAppDetail("iphone-6.5in")

    @Test
    @Config(qualifiers = IPAD_13IN)
    fun appDetailIpad13in() = captureAppDetail("ipad-13in")

    private fun captureDashboardToday(device: String) = capture(device, "01_dashboard_today") {
        DashboardScreenContent(
            state = DashboardState(
                selectedPeriod = Period.TODAY,
                summary = sampleSummary(Period.TODAY, earnings = 24.61, impressions = 18_432, clicks = 402),
                yesterdaySummary = RevenueSummary(
                    period = Period.TODAY,
                    currencyCode = "USD",
                    earnings = 19.38,
                    impressions = 15_204,
                    clicks = 341,
                    previousEarnings = null,
                    deltaPercent = null
                ),
                apps = sampleApps()
            ),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }

    private fun captureDashboardMonth(device: String) = capture(device, "02_dashboard_month") {
        DashboardScreenContent(
            state = DashboardState(
                selectedPeriod = Period.LAST_30_DAYS,
                summary = sampleSummary(Period.LAST_30_DAYS, earnings = 612.84, impressions = 480_112, clicks = 10_982),
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

    private fun captureDashboardYear(device: String) = capture(device, "04_dashboard_year") {
        DashboardScreenContent(
            state = DashboardState(
                selectedPeriod = Period.LAST_365_DAYS,
                summary = sampleSummary(
                    Period.LAST_365_DAYS,
                    earnings = 7_412.36,
                    impressions = 5_812_940,
                    clicks = 131_206
                ),
                apps = sampleApps(),
                series = sampleWeeklySeries()
            ),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }

    private fun captureDashboardLifetime(device: String) = capture(device, "05_dashboard_lifetime") {
        DashboardScreenContent(
            state = DashboardState(
                selectedPeriod = Period.LIFETIME,
                summary = sampleSummary(
                    Period.LIFETIME,
                    earnings = 19_845.02,
                    impressions = 16_204_118,
                    clicks = 361_842
                ),
                apps = sampleApps(),
                series = sampleMonthlySeries()
            ),
            onPeriodSelected = {},
            onRefresh = {},
            onRetry = {},
            onAppClick = {},
            onSettingsClick = {}
        )
    }

    private fun captureAppDetail(device: String) = capture(device, "03_app_detail") {
        AppDetailScreenContent(
            state = AppDetailState(
                appName = "Currency Converter",
                period = Period.LAST_30_DAYS,
                isLoading = false,
                app = AppRevenue("app1", "Currency Converter", 341.52, 261_040, 6_120),
                series = sampleSeries(),
                formats = sampleFormats(),
                currencyCode = "USD"
            ),
            onBackClick = {}
        )
    }

    private fun sampleFormats() = listOf(
        FormatRevenue(AdFormat.REWARDED, "Rewarded", 182.31, 96_420, 3_140),
        FormatRevenue(AdFormat.INTERSTITIAL, "Interstitial", 104.68, 88_210, 2_010),
        FormatRevenue(AdFormat.BANNER, "Banner", 54.53, 76_410, 970)
    )

    private fun sampleSummary(period: Period, earnings: Double, impressions: Long, clicks: Long) =
        RevenueSummary(
            period = period,
            currencyCode = "USD",
            earnings = earnings,
            impressions = impressions,
            clicks = clicks,
            previousEarnings = earnings / 1.27,
            deltaPercent = 27.0
        )

    private fun sampleApps() = listOf(
        AppRevenue("app1", "Currency Converter", 11.84, 9_004, 201),
        AppRevenue("app2", "TraceFit", 7.42, 5_420, 118),
        AppRevenue("app3", "Recipe Vault", 5.35, 3_918, 83)
    )

    // A month of plausible daily earnings: weekly rhythm plus a gentle upward trend.
    private fun sampleSeries() = (1..30).map { day ->
        RevenuePoint(
            date = LocalDate(2026, 6, day),
            earnings = 14.0 + (day % 7) * 2.3 + day * 0.35
        )
    }

    // 52 Monday-dated weekly buckets, matching what the repository produces for LAST_365_DAYS.
    private fun sampleWeeklySeries() = (0..51).map { week ->
        RevenuePoint(
            date = LocalDate(2025, 7, 14).plus(DatePeriod(days = week * 7)),
            earnings = 110.0 + (week % 9) * 11.0 + week * 1.4
        )
    }

    // Three years of month-start buckets, matching what the repository produces for LIFETIME.
    private fun sampleMonthlySeries() = (0..35).map { month ->
        RevenuePoint(
            date = LocalDate(2023, 7, 1).plus(DatePeriod(months = month)),
            earnings = 220.0 + month * 14.5 + (month % 5) * 32.0
        )
    }

    // Each scene is captured in both themes: the light listing keeps its original name, the dark
    // variant gets a `_dark` suffix, so store listings can offer light + dark screenshots.
    private fun capture(device: String, name: String, content: @Composable () -> Unit) {
        // Tests run with the module directory as cwd; the screenshots live in the repo root's art/.
        listOf(false to name, true to "${name}_dark").forEach { (dark, fileName) ->
            captureRoboImage(filePath = "../art/store/screenshots/$device/$fileName.png") {
                AdTrackTheme(darkTheme = dark) { content() }
            }
        }
    }
}
