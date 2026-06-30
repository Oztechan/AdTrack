/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.admob.mapper

import com.oztechan.adtrace.data.admob.api.AdMobApi
import com.oztechan.adtrace.data.admob.model.ReportRow
import com.oztechan.adtrace.domain.model.AppRevenue
import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.domain.model.RevenuePoint
import com.oztechan.adtrace.domain.model.RevenueSummary
import kotlinx.datetime.LocalDate

/** Maps raw AdMob report rows into domain models (micros -> currency, int64 strings -> Long). */
object ReportMapper {

    private const val MICROS_PER_UNIT = 1_000_000.0
    private const val PERCENT = 100.0
    private const val DATE_LENGTH = 8

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
                clicks = row.integerMetric(AdMobApi.Metric.CLICKS)
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

    // AdMob DATE dimension is encoded as "YYYYMMDD".
    private fun parseDate(raw: String): LocalDate? {
        if (raw.length != DATE_LENGTH) return null
        val year = raw.substring(0, 4).toIntOrNull()
        val month = raw.substring(4, 6).toIntOrNull()
        val day = raw.substring(6, 8).toIntOrNull()
        return if (year != null && month != null && day != null) {
            runCatching { LocalDate(year, month, day) }.getOrNull()
        } else {
            null
        }
    }
}
