/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.demo

import com.oztechan.adtrack.data.admob.model.AdMobAccount
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.FormatRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import com.oztechan.adtrack.domain.repository.RevenueRepository

/**
 * [RevenueRepository] bound app-wide. While [DemoManager.isDemo] is on it serves [DemoData] sample
 * data; otherwise it delegates to the real AdMob-backed [real] repository unchanged. This keeps demo
 * logic out of the real data path.
 */
class DemoRevenueRepository(
    private val demoManager: DemoManager,
    private val real: RevenueRepository
) : RevenueRepository {

    override suspend fun getAccount(): AdMobAccount =
        if (demoManager.isDemo) DemoData.account else real.getAccount()

    override suspend fun getSummary(period: Period): RevenueSummary =
        if (demoManager.isDemo) DemoData.summary(period) else real.getSummary(period)

    override suspend fun getYesterdaySummary(): RevenueSummary =
        if (demoManager.isDemo) DemoData.yesterdaySummary() else real.getYesterdaySummary()

    override suspend fun getAppBreakdown(period: Period): List<AppRevenue> =
        if (demoManager.isDemo) DemoData.apps(period) else real.getAppBreakdown(period)

    override suspend fun getRevenueSeries(period: Period): List<RevenuePoint> =
        if (demoManager.isDemo) DemoData.series(period) else real.getRevenueSeries(period)

    override suspend fun getAppRevenueSeries(period: Period, appId: String): List<RevenuePoint> =
        if (demoManager.isDemo) DemoData.series(period) else real.getAppRevenueSeries(period, appId)

    override suspend fun getFormatBreakdown(period: Period, appId: String): List<FormatRevenue> =
        if (demoManager.isDemo) DemoData.formats() else real.getFormatBreakdown(period, appId)

    override fun invalidate() = real.invalidate()
}
