/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.domain.model

import kotlinx.datetime.LocalDate

enum class Period {
    TODAY,
    LAST_7_DAYS,
    THIS_MONTH,
    LAST_90_DAYS
}

data class RevenueSummary(
    val period: Period,
    val currencyCode: String,
    val earnings: Double,
    val impressions: Long,
    val clicks: Long,
    val previousEarnings: Double?,
    val deltaPercent: Double?
)

data class AppRevenue(
    val appId: String,
    val appName: String,
    val earnings: Double,
    val impressions: Long,
    val clicks: Long
)

data class RevenuePoint(
    val date: LocalDate,
    val earnings: Double
)
