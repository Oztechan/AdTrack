/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain.repository

import com.oztechan.adtrack.data.admob.model.DimensionValue
import com.oztechan.adtrack.data.admob.model.MetricValue
import com.oztechan.adtrack.data.admob.model.ReportRow
import com.oztechan.adtrack.domain.PeriodCalculator
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.fakes.FakeAdMobApi
import com.oztechan.adtrack.fakes.fakeAccount
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RevenueRepositoryImplTest {

    private val tz = "UTC"
    private val calculator = PeriodCalculator()

    private fun earningsRow(micros: String) = ReportRow(
        metricValues = mapOf("ESTIMATED_EARNINGS" to MetricValue(microsValue = micros))
    )

    private fun datedRow(date: String, micros: String) = ReportRow(
        dimensionValues = mapOf("DATE" to DimensionValue(date)),
        metricValues = mapOf("ESTIMATED_EARNINGS" to MetricValue(microsValue = micros))
    )

    private fun repo(api: FakeAdMobApi) = RevenueRepositoryImpl(api, calculator)

    @Test
    fun yearly_series_is_aggregated_weekly_and_lifetime_monthly() = runTest {
        val api = FakeAdMobApi { _, _ ->
            listOf(
                datedRow("20260105", "1000000"), // Monday
                datedRow("20260107", "2000000"), // same week
                datedRow("20260112", "4000000") // next Monday
            )
        }

        val weekly = repo(api).getRevenueSeries(Period.LAST_365_DAYS)
        assertEquals(2, weekly.size)
        assertEquals(3.0, weekly[0].earnings)
        assertEquals(4.0, weekly[1].earnings)

        val monthly = repo(api).getRevenueSeries(Period.LIFETIME)
        assertEquals(1, monthly.size)
        assertEquals(7.0, monthly[0].earnings)

        val daily = repo(api).getRevenueSeries(Period.LAST_30_DAYS)
        assertEquals(3, daily.size)
    }

    @Test
    fun getAccount_returns_first_account() = runTest {
        val api = FakeAdMobApi(accounts = listOf(fakeAccount(publisherId = "pub-1")))
        assertEquals("pub-1", repo(api).getAccount().publisherId)
    }

    @Test
    fun empty_account_list_throws_NoAdMobAccountException() = runTest {
        val api = FakeAdMobApi(accounts = emptyList())
        assertFailsWith<NoAdMobAccountException> { repo(api).getAccount() }
    }

    @Test
    fun summary_sums_current_rows_and_computes_delta_from_previous() = runTest {
        val current = calculator.currentRange(Period.TODAY, tz)
        val previous = calculator.previousRange(Period.TODAY, tz)
        val api = FakeAdMobApi { _, spec ->
            when (spec.dateRange) {
                current -> listOf(earningsRow("3000000")) // 3.0
                previous -> listOf(earningsRow("2000000")) // 2.0
                else -> emptyList()
            }
        }

        val summary = repo(api).getSummary(Period.TODAY)

        assertEquals(3.0, summary.earnings)
        assertEquals(2.0, summary.previousEarnings)
        assertEquals(50.0, summary.deltaPercent) // (3-2)/2 * 100
    }

    @Test
    fun yesterday_summary_reports_over_yesterdays_range_without_delta() = runTest {
        val yesterday = calculator.previousRange(Period.TODAY, tz)
        val api = FakeAdMobApi { _, spec ->
            if (spec.dateRange == yesterday) listOf(earningsRow("2000000")) else emptyList()
        }

        val summary = repo(api).getYesterdaySummary()

        assertEquals(2.0, summary.earnings)
        assertEquals(null, summary.previousEarnings)
        assertEquals(null, summary.deltaPercent)
    }

    @Test
    fun reads_sharing_the_same_report_reuse_one_api_call() = runTest {
        val api = FakeAdMobApi { _, _ -> listOf(earningsRow("1000000")) }
        val repository = repo(api)

        repository.getSummary(Period.TODAY) // fetches today's + yesterday's reports
        assertEquals(2, api.reportCallCount)

        repository.getRevenueSeries(Period.TODAY) // same report as summary's current range
        repository.getYesterdaySummary() // same report as summary's previous range
        assertEquals(2, api.reportCallCount, "Series and yesterday reads should reuse cached reports")
    }

    @Test
    fun summary_is_cached_until_invalidated() = runTest {
        val api = FakeAdMobApi { _, _ -> listOf(earningsRow("1000000")) }
        val repository = repo(api)

        repository.getSummary(Period.TODAY)
        val afterFirst = api.reportCallCount
        repository.getSummary(Period.TODAY)
        assertEquals(afterFirst, api.reportCallCount, "Second call should hit the cache")

        repository.invalidate()
        repository.getSummary(Period.TODAY)
        assertTrue(api.reportCallCount > afterFirst, "Invalidate should force a refetch")
    }

    @Test
    fun app_revenue_series_sends_app_dimension_filter() = runTest {
        val api = FakeAdMobApi { _, _ ->
            listOf(
                ReportRow(
                    dimensionValues = mapOf("DATE" to DimensionValue("20260101")),
                    metricValues = mapOf("ESTIMATED_EARNINGS" to MetricValue(microsValue = "1000000"))
                )
            )
        }
        repo(api).getAppRevenueSeries(Period.LAST_30_DAYS, appId = "app-77")

        val filter = api.specs.last().dimensionFilters?.firstOrNull()
        assertEquals("APP", filter?.dimension)
        assertEquals(listOf("app-77"), filter?.matchesAny?.values)
    }

    @Test
    fun app_breakdown_requests_app_and_platform_dimensions() = runTest {
        val api = FakeAdMobApi { _, _ -> emptyList() }
        repo(api).getAppBreakdown(Period.LAST_90_DAYS)
        assertTrue(api.specs.last().dimensions.containsAll(listOf("APP", "PLATFORM")))
    }
}
