/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain.repository

import com.oztechan.adtrack.data.admob.model.AdMobAccount
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.FormatRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary

interface RevenueRepository {
    suspend fun getAccount(): AdMobAccount
    suspend fun getSummary(period: Period): RevenueSummary

    /** Yesterday's totals, shown next to today's summary where there is no chart to give context. */
    suspend fun getYesterdaySummary(): RevenueSummary

    suspend fun getAppBreakdown(period: Period): List<AppRevenue>
    suspend fun getRevenueSeries(period: Period): List<RevenuePoint>

    /** Daily revenue series for a single app (DATE dimension filtered by APP). */
    suspend fun getAppRevenueSeries(period: Period, appId: String): List<RevenuePoint>

    /** Revenue split by ad format for a single app (FORMAT dimension filtered by APP). */
    suspend fun getFormatBreakdown(period: Period, appId: String): List<FormatRevenue>

    /** Drops the in-memory cache so the next read fetches fresh data (pull-to-refresh). */
    fun invalidate()
}
