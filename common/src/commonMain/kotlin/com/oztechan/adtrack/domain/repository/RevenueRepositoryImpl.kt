/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain.repository

import com.oztechan.adtrack.data.admob.api.AdMobApi
import com.oztechan.adtrack.data.admob.mapper.ReportMapper
import com.oztechan.adtrack.data.admob.model.AdMobAccount
import com.oztechan.adtrack.data.admob.model.DateRange
import com.oztechan.adtrack.data.admob.model.DimensionFilter
import com.oztechan.adtrack.data.admob.model.LocalizationSettings
import com.oztechan.adtrack.data.admob.model.NetworkReportSpec
import com.oztechan.adtrack.data.admob.model.ReportRow
import com.oztechan.adtrack.data.admob.model.StringList
import com.oztechan.adtrack.domain.PeriodCalculator
import com.oztechan.adtrack.domain.SeriesAggregator
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import com.oztechan.adtrack.domain.seriesGranularity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

private const val CACHE_TTL_SECONDS = 300L

class RevenueRepositoryImpl(
    private val adMobApi: AdMobApi,
    private val periodCalculator: PeriodCalculator
) : RevenueRepository {

    private val cache = mutableMapOf<String, CacheEntry>()
    private val locks = mutableMapOf<String, Mutex>()
    private var cachedAccount: AdMobAccount? = null

    override suspend fun getAccount(): AdMobAccount =
        cachedAccount ?: adMobApi.getAccounts().firstOrNull()?.also { cachedAccount = it }
            ?: throw NoAdMobAccountException()

    override suspend fun getSummary(period: Period): RevenueSummary = cached("summary_$period") {
        val account = getAccount()
        val current = report(account, periodCalculator.currentRange(period, account.reportingTimeZone))
        val previousEarnings = periodCalculator.previousRange(period, account.reportingTimeZone)
            ?.let { ReportMapper.totalEarnings(report(account, it)) }
        ReportMapper.toSummary(
            rows = current,
            period = period,
            currencyCode = account.currencyCode,
            previousEarnings = previousEarnings
        )
    }

    override suspend fun getYesterdaySummary(): RevenueSummary = cached("summary_yesterday") {
        val account = getAccount()
        // TODAY's previous range is exactly yesterday.
        val range = requireNotNull(periodCalculator.previousRange(Period.TODAY, account.reportingTimeZone))
        ReportMapper.toSummary(
            rows = report(account, range),
            period = Period.TODAY,
            currencyCode = account.currencyCode,
            previousEarnings = null
        )
    }

    override suspend fun getAppBreakdown(period: Period): List<AppRevenue> = cached("apps_$period") {
        val account = getAccount()
        val rows = report(
            account = account,
            range = periodCalculator.currentRange(period, account.reportingTimeZone),
            dimensions = listOf(AdMobApi.Dimension.APP, AdMobApi.Dimension.PLATFORM)
        )
        ReportMapper.toAppRevenues(rows)
    }

    override suspend fun getRevenueSeries(period: Period): List<RevenuePoint> = cached("series_$period") {
        val account = getAccount()
        val rows = report(account, periodCalculator.currentRange(period, account.reportingTimeZone))
        SeriesAggregator.aggregate(ReportMapper.toSeries(rows), period.seriesGranularity)
    }

    override suspend fun getAppRevenueSeries(
        period: Period,
        appId: String
    ): List<RevenuePoint> = cached("appseries_${period}_$appId") {
        val account = getAccount()
        val rows = report(
            account = account,
            range = periodCalculator.currentRange(period, account.reportingTimeZone),
            appIdFilter = appId.takeIf { it.isNotBlank() }
        )
        SeriesAggregator.aggregate(ReportMapper.toSeries(rows), period.seriesGranularity)
    }

    override fun invalidate() {
        cache.clear()
        cachedAccount = null
    }

    // Cached at the request level so reads sharing the same report (e.g. TODAY's summary, its
    // series, and yesterday's card) cost a single AdMob API call.
    private suspend fun report(
        account: AdMobAccount,
        range: DateRange,
        dimensions: List<String> = listOf(AdMobApi.Dimension.DATE),
        appIdFilter: String? = null
    ): List<ReportRow> = cached("report_${range}_${dimensions}_$appIdFilter") {
        adMobApi.generateNetworkReport(
            publisherId = account.publisherId,
            spec = NetworkReportSpec(
                dateRange = range,
                dimensions = dimensions,
                metrics = listOf(
                    AdMobApi.Metric.ESTIMATED_EARNINGS,
                    AdMobApi.Metric.IMPRESSIONS,
                    AdMobApi.Metric.CLICKS
                ),
                localizationSettings = LocalizationSettings(currencyCode = account.currencyCode),
                dimensionFilters = appIdFilter?.let {
                    listOf(DimensionFilter(AdMobApi.Dimension.APP, StringList(listOf(it))))
                }
            )
        )
    }

    // Per-key lock so concurrent readers of the same key (dashboard loads in parallel) wait for
    // the first fetch instead of firing duplicate API calls.
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> cached(key: String, block: suspend () -> T): T =
        locks.getOrPut(key) { Mutex() }.withLock {
            val now = Clock.System.now().epochSeconds
            val hit = cache[key]?.takeIf { now < it.expiry }
            hit?.value as T? ?: block().also { cache[key] = CacheEntry(it as Any, now + CACHE_TTL_SECONDS) }
        }

    private data class CacheEntry(val value: Any, val expiry: Long)
}
