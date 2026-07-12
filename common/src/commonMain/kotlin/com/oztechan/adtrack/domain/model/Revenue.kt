/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain.model

import kotlinx.datetime.LocalDate

enum class Period {
    TODAY,
    LAST_30_DAYS,
    LAST_90_DAYS,
    LAST_365_DAYS,
    LIFETIME
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

enum class AppPlatform {
    ANDROID,
    IOS,
    UNKNOWN
}

data class AppRevenue(
    val appId: String,
    val appName: String,
    val earnings: Double,
    val impressions: Long,
    val clicks: Long,
    val platform: AppPlatform = AppPlatform.UNKNOWN
)

data class RevenuePoint(
    val date: LocalDate,
    val earnings: Double
)
