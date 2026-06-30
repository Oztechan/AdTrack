/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.admob.mapper

import com.oztechan.adtrace.data.admob.model.DimensionValue
import com.oztechan.adtrace.data.admob.model.MetricValue
import com.oztechan.adtrace.data.admob.model.ReportRow
import com.oztechan.adtrace.domain.model.Period
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReportMapperTest {

    private fun row(
        earningsMicros: String? = null,
        impressions: String? = null,
        clicks: String? = null,
        dimensions: Map<String, DimensionValue> = emptyMap()
    ) = ReportRow(
        dimensionValues = dimensions,
        metricValues = mapOf(
            "ESTIMATED_EARNINGS" to MetricValue(microsValue = earningsMicros),
            "IMPRESSIONS" to MetricValue(integerValue = impressions),
            "CLICKS" to MetricValue(integerValue = clicks)
        )
    )

    @Test
    fun micros_are_converted_to_currency_units() {
        val rows = listOf(row(earningsMicros = "1500000"), row(earningsMicros = "2500000"))
        assertEquals(4.0, ReportMapper.totalEarnings(rows))
    }

    @Test
    fun summary_aggregates_metrics_and_computes_delta() {
        val rows = listOf(row(earningsMicros = "1500000", impressions = "100", clicks = "5"))
        val summary = ReportMapper.toSummary(rows, Period.TODAY, "USD", previousEarnings = 1.0)

        assertEquals(1.5, summary.earnings)
        assertEquals(100L, summary.impressions)
        assertEquals(5L, summary.clicks)
        assertEquals(50.0, summary.deltaPercent)
    }

    @Test
    fun delta_is_null_when_previous_is_zero_or_missing() {
        val rows = listOf(row(earningsMicros = "1000000"))
        assertNull(ReportMapper.toSummary(rows, Period.TODAY, "USD", previousEarnings = 0.0).deltaPercent)
        assertNull(ReportMapper.toSummary(rows, Period.TODAY, "USD", previousEarnings = null).deltaPercent)
    }

    @Test
    fun app_breakdown_sorts_by_earnings_desc_and_uses_display_label() {
        val rows = listOf(
            row(earningsMicros = "1000000", dimensions = mapOf("APP" to DimensionValue("app1", "Small App"))),
            row(earningsMicros = "9000000", dimensions = mapOf("APP" to DimensionValue("app2", "Big App")))
        )
        val apps = ReportMapper.toAppRevenues(rows)
        assertEquals("Big App", apps.first().appName)
        assertEquals("app2", apps.first().appId)
        assertEquals(9.0, apps.first().earnings)
    }

    @Test
    fun series_parses_yyyymmdd_and_sorts_by_date() {
        val rows = listOf(
            row(earningsMicros = "2000000", dimensions = mapOf("DATE" to DimensionValue("20260115"))),
            row(earningsMicros = "1000000", dimensions = mapOf("DATE" to DimensionValue("20260110")))
        )
        val series = ReportMapper.toSeries(rows)
        assertEquals(2, series.size)
        assertTrue(series[0].date < series[1].date)
        assertEquals(LocalDate(2026, 1, 10), series[0].date)
    }

    @Test
    fun empty_rows_produce_zeroed_summary() {
        val summary = ReportMapper.toSummary(emptyList(), Period.TODAY, "USD", previousEarnings = null)
        assertEquals(0.0, summary.earnings)
        assertEquals(0L, summary.impressions)
        assertEquals(0L, summary.clicks)
        assertEquals(0.0, ReportMapper.totalEarnings(emptyList()))
    }

    @Test
    fun missing_metric_values_default_to_zero() {
        val row = ReportRow(metricValues = emptyMap())
        assertEquals(0.0, ReportMapper.totalEarnings(listOf(row)))
    }

    @Test
    fun malformed_date_rows_are_skipped_in_series() {
        val rows = listOf(
            row(earningsMicros = "1000000", dimensions = mapOf("DATE" to DimensionValue("2026-01-10"))),
            row(earningsMicros = "1000000", dimensions = mapOf("DATE" to DimensionValue("notadate"))),
            row(earningsMicros = "1000000", dimensions = mapOf("DATE" to DimensionValue("20260112")))
        )
        // Only the well-formed YYYYMMDD value parses.
        val series = ReportMapper.toSeries(rows)
        assertEquals(1, series.size)
        assertEquals(LocalDate(2026, 1, 12), series.first().date)
    }

    @Test
    fun blank_display_label_falls_back_to_app_id() {
        val rows = listOf(
            row(earningsMicros = "1000000", dimensions = mapOf("APP" to DimensionValue("app-x", "")))
        )
        assertEquals("app-x", ReportMapper.toAppRevenues(rows).first().appName)
    }
}
