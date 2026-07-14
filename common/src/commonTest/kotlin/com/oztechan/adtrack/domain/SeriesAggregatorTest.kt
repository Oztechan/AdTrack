/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain

import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SeriesAggregatorTest {

    @Test
    fun periods_map_to_expected_granularity() {
        assertEquals(SeriesGranularity.DAILY, Period.TODAY.seriesGranularity)
        assertEquals(SeriesGranularity.DAILY, Period.LAST_30_DAYS.seriesGranularity)
        assertEquals(SeriesGranularity.DAILY, Period.LAST_90_DAYS.seriesGranularity)
        assertEquals(SeriesGranularity.WEEKLY, Period.LAST_365_DAYS.seriesGranularity)
        assertEquals(SeriesGranularity.MONTHLY, Period.LIFETIME.seriesGranularity)
    }

    @Test
    fun daily_granularity_returns_points_untouched() {
        val points = listOf(RevenuePoint(LocalDate(2026, 1, 5), 1.0))
        assertSame(points, SeriesAggregator.aggregate(points, SeriesGranularity.DAILY))
    }

    @Test
    fun weekly_granularity_buckets_days_into_their_monday() {
        // 2026-01-05 is a Monday; 2026-01-12 starts the next week.
        val points = listOf(
            RevenuePoint(LocalDate(2026, 1, 5), 1.0), // Monday, week 1
            RevenuePoint(LocalDate(2026, 1, 7), 2.0), // Wednesday, week 1
            RevenuePoint(LocalDate(2026, 1, 11), 3.0), // Sunday, week 1
            RevenuePoint(LocalDate(2026, 1, 12), 4.0) // Monday, week 2
        )

        val weekly = SeriesAggregator.aggregate(points, SeriesGranularity.WEEKLY)

        assertEquals(2, weekly.size)
        assertEquals(RevenuePoint(LocalDate(2026, 1, 5), 6.0), weekly[0])
        assertEquals(RevenuePoint(LocalDate(2026, 1, 12), 4.0), weekly[1])
    }

    @Test
    fun monthly_granularity_buckets_days_into_the_first_of_month() {
        val points = listOf(
            RevenuePoint(LocalDate(2026, 2, 28), 5.0),
            RevenuePoint(LocalDate(2026, 1, 15), 1.0),
            RevenuePoint(LocalDate(2026, 1, 31), 2.0)
        )

        val monthly = SeriesAggregator.aggregate(points, SeriesGranularity.MONTHLY)

        assertEquals(2, monthly.size)
        assertEquals(RevenuePoint(LocalDate(2026, 1, 1), 3.0), monthly[0])
        assertEquals(RevenuePoint(LocalDate(2026, 2, 1), 5.0), monthly[1])
    }

    @Test
    fun buckets_are_sorted_by_date() {
        val points = listOf(
            RevenuePoint(LocalDate(2026, 3, 1), 1.0),
            RevenuePoint(LocalDate(2026, 1, 1), 2.0),
            RevenuePoint(LocalDate(2026, 2, 1), 3.0)
        )

        val monthly = SeriesAggregator.aggregate(points, SeriesGranularity.MONTHLY)

        assertEquals(
            listOf(LocalDate(2026, 1, 1), LocalDate(2026, 2, 1), LocalDate(2026, 3, 1)),
            monthly.map { it.date }
        )
    }

    @Test
    fun empty_series_stays_empty() {
        assertEquals(emptyList(), SeriesAggregator.aggregate(emptyList(), SeriesGranularity.WEEKLY))
        assertEquals(emptyList(), SeriesAggregator.aggregate(emptyList(), SeriesGranularity.MONTHLY))
    }
}
