/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.demo

import com.oztechan.adtrack.data.admob.model.AdMobAccount
import com.oztechan.adtrack.domain.model.AdFormat
import com.oztechan.adtrack.domain.model.AppPlatform
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.FormatRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.sin
import kotlin.time.Clock

/**
 * Deterministic sample data for [DemoRevenueRepository]. Numbers are plausible but obviously
 * belong to a "Demo account", so a reviewer (or a curious user) sees a realistic dashboard without
 * a real AdMob account. Values scale by the selected period's day count.
 */
@Suppress("MagicNumber") // hand-tuned sample figures
internal object DemoData {

    val account = AdMobAccount(
        name = "accounts/demo",
        publisherId = "pub-DEMO000000000000",
        currencyCode = "USD",
        reportingTimeZone = "UTC"
    )

    fun summary(period: Period): RevenueSummary {
        val days = period.days
        val earnings = DAILY_EARNINGS * days
        val previous = earnings * 0.88
        return RevenueSummary(
            period = period,
            currencyCode = account.currencyCode,
            earnings = earnings,
            impressions = DAILY_IMPRESSIONS * days,
            clicks = DAILY_CLICKS * days,
            previousEarnings = previous,
            deltaPercent = (earnings - previous) / previous * 100
        )
    }

    fun yesterdaySummary(): RevenueSummary = summary(Period.TODAY).copy(
        earnings = DAILY_EARNINGS * 0.92,
        previousEarnings = null,
        deltaPercent = null
    )

    fun apps(period: Period): List<AppRevenue> {
        val days = period.days
        return listOf(
            AppRevenue(
                appId = "demo-weather-android",
                appName = "Weather Live",
                earnings = 6.1 * days,
                impressions = 820L * days,
                clicks = 15L * days,
                platform = AppPlatform.ANDROID
            ),
            AppRevenue(
                appId = "demo-weather-ios",
                appName = "Weather Live",
                earnings = 4.3 * days,
                impressions = 540L * days,
                clicks = 9L * days,
                platform = AppPlatform.IOS
            ),
            AppRevenue(
                appId = "demo-puzzle-android",
                appName = "Puzzle Blocks",
                earnings = 3.8 * days,
                impressions = 610L * days,
                clicks = 11L * days,
                platform = AppPlatform.ANDROID
            )
        )
    }

    fun formats(): List<FormatRevenue> = listOf(
        FormatRevenue(AdFormat.BANNER, "Banner", 3.1, 980, 6),
        FormatRevenue(AdFormat.INTERSTITIAL, "Interstitial", 7.4, 540, 21),
        FormatRevenue(AdFormat.REWARDED, "Rewarded", 3.7, 160, 7)
    )

    // A series whose point count, spacing and scale all vary by period, so switching periods visibly
    // changes the chart (like the real data differs between, say, 30 and 90 days). Each point is one
    // bucket (day / week / month) worth of earnings, waving gently; the last bucket ends today.
    fun series(period: Period): List<RevenuePoint> {
        val count = period.points
        val stepDays = (period.days / count).coerceAtLeast(1)
        val perBucket = DAILY_EARNINGS * stepDays
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return (0 until count).map { index ->
            RevenuePoint(
                date = today.minus(DatePeriod(days = (count - 1 - index) * stepDays)),
                earnings = perBucket * (1.0 + 0.3 * sin(index.toDouble()))
            )
        }
    }

    private val Period.days: Int
        get() = when (this) {
            Period.TODAY -> 1
            Period.LAST_30_DAYS -> 30
            Period.LAST_90_DAYS -> 90
            Period.LAST_365_DAYS -> 365
            Period.LIFETIME -> 540
        }

    // Number of chart buckets per period: daily for 30 days, ~weekly for 90, ~monthly for a year+.
    private val Period.points: Int
        get() = when (this) {
            Period.TODAY -> 1
            Period.LAST_30_DAYS -> 30
            Period.LAST_90_DAYS -> 13
            Period.LAST_365_DAYS -> 12
            Period.LIFETIME -> 18
        }

    private const val DAILY_EARNINGS = 14.20
    private const val DAILY_IMPRESSIONS = 1_900L
    private const val DAILY_CLICKS = 34L
}
