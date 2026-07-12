/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.admob.mapper

import com.oztechan.adtrack.data.admob.api.AdMobApi
import com.oztechan.adtrack.data.admob.model.ReportRow
import com.oztechan.adtrack.domain.model.AppPlatform
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import kotlinx.datetime.LocalDate

/** Maps raw AdMob report rows into domain models (micros -> currency, int64 strings -> Long). */
object ReportMapper {

    private const val MICROS_PER_UNIT = 1_000_000.0
    private const val PERCENT = 100.0
    private const val DATE_LENGTH = 8

    // "YYYYMMDD" field boundaries.
    private const val YEAR_END = 4
    private const val MONTH_END = 6

    fun toSummary(
        rows: List<ReportRow>,
        period: Period,
        currencyCode: String,
        previousEarnings: Double?
    ): RevenueSummary {
        val earnings = totalEarnings(rows)
        return RevenueSummary(
            period = period,
            currencyCode = currencyCode,
            earnings = earnings,
            impressions = rows.sumOf { it.integerMetric(AdMobApi.Metric.IMPRESSIONS) },
            clicks = rows.sumOf { it.integerMetric(AdMobApi.Metric.CLICKS) },
            previousEarnings = previousEarnings,
            deltaPercent = deltaPercent(earnings, previousEarnings)
        )
    }

    fun toAppRevenues(rows: List<ReportRow>): List<AppRevenue> = rows
        .map { row ->
            val app = row.dimensionValues[AdMobApi.Dimension.APP]
            AppRevenue(
                appId = app?.value.orEmpty(),
                appName = app?.displayLabel?.takeIf { it.isNotBlank() } ?: app?.value.orEmpty(),
                earnings = row.earnings(),
                impressions = row.integerMetric(AdMobApi.Metric.IMPRESSIONS),
                clicks = row.integerMetric(AdMobApi.Metric.CLICKS),
                platform = row.platform()
            )
        }
        .sortedByDescending { it.earnings }

    fun toSeries(rows: List<ReportRow>): List<RevenuePoint> = rows
        .mapNotNull { row ->
            row.dimensionValues[AdMobApi.Dimension.DATE]?.value
                ?.let { parseDate(it) }
                ?.let { RevenuePoint(date = it, earnings = row.earnings()) }
        }
        .sortedBy { it.date }

    fun totalEarnings(rows: List<ReportRow>): Double = rows.sumOf { it.earnings() }

    private fun deltaPercent(current: Double, previous: Double?): Double? =
        if (previous != null && previous != 0.0) (current - previous) / previous * PERCENT else null

    private fun ReportRow.earnings(): Double =
        (metricValues[AdMobApi.Metric.ESTIMATED_EARNINGS]?.microsValue?.toDoubleOrNull() ?: 0.0) / MICROS_PER_UNIT

    private fun ReportRow.integerMetric(metric: String): Long =
        metricValues[metric]?.integerValue?.toLongOrNull() ?: 0L

    private fun ReportRow.platform(): AppPlatform =
        dimensionValues[AdMobApi.Dimension.PLATFORM]?.value
            ?.let { raw -> AppPlatform.entries.firstOrNull { it.name == raw.uppercase() } }
            ?: AppPlatform.UNKNOWN

    // AdMob DATE dimension is encoded as "YYYYMMDD".
    private fun parseDate(raw: String): LocalDate? {
        if (raw.length != DATE_LENGTH) return null
        val year = raw.substring(0, YEAR_END).toIntOrNull()
        val month = raw.substring(YEAR_END, MONTH_END).toIntOrNull()
        val day = raw.substring(MONTH_END, DATE_LENGTH).toIntOrNull()
        return if (year != null && month != null && day != null) {
            runCatching { LocalDate(year, month, day) }.getOrNull()
        } else {
            null
        }
    }
}
