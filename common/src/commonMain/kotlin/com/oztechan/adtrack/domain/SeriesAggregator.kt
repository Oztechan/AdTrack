/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain

import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus

enum class SeriesGranularity {
    DAILY,
    WEEKLY,
    MONTHLY
}

/**
 * Chart resolution per period: short ranges stay day-by-day, a year reads better week-by-week,
 * and lifetime month-by-month (a daily lifetime series would be thousands of unreadable bars).
 */
val Period.seriesGranularity: SeriesGranularity
    get() = when (this) {
        Period.TODAY,
        Period.LAST_30_DAYS,
        Period.LAST_90_DAYS -> SeriesGranularity.DAILY
        Period.LAST_365_DAYS -> SeriesGranularity.WEEKLY
        Period.LIFETIME -> SeriesGranularity.MONTHLY
    }

/**
 * Buckets a daily revenue series into the requested granularity, summing earnings per bucket.
 * Each aggregated point is dated at its bucket start (Monday for weeks, the 1st for months).
 */
object SeriesAggregator {

    fun aggregate(points: List<RevenuePoint>, granularity: SeriesGranularity): List<RevenuePoint> =
        when (granularity) {
            SeriesGranularity.DAILY -> points
            SeriesGranularity.WEEKLY -> bucket(points) { it.startOfWeek() }
            SeriesGranularity.MONTHLY -> bucket(points) { LocalDate(it.year, it.month, 1) }
        }

    private fun bucket(
        points: List<RevenuePoint>,
        bucketStart: (LocalDate) -> LocalDate
    ): List<RevenuePoint> = points
        .groupBy { bucketStart(it.date) }
        .map { (start, days) -> RevenuePoint(date = start, earnings = days.sumOf { it.earnings }) }
        .sortedBy { it.date }

    private fun LocalDate.startOfWeek(): LocalDate = minus(DatePeriod(days = dayOfWeek.isoDayNumber - 1))
}
