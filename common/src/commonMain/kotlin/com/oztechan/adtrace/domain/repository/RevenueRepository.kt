/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.domain.repository

import com.oztechan.adtrace.data.admob.model.AdMobAccount
import com.oztechan.adtrace.domain.model.AppRevenue
import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.domain.model.RevenuePoint
import com.oztechan.adtrace.domain.model.RevenueSummary

interface RevenueRepository {
    suspend fun getAccount(): AdMobAccount
    suspend fun getSummary(period: Period): RevenueSummary
    suspend fun getAppBreakdown(period: Period): List<AppRevenue>
    suspend fun getRevenueSeries(period: Period): List<RevenuePoint>

    /** Daily revenue series for a single app (DATE dimension filtered by APP). */
    suspend fun getAppRevenueSeries(period: Period, appId: String): List<RevenuePoint>

    /** Drops the in-memory cache so the next read fetches fresh data (pull-to-refresh). */
    fun invalidate()
}
