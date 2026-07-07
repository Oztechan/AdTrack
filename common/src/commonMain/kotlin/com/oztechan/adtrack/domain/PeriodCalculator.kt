/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain

import com.oztechan.adtrack.data.admob.model.ApiDate
import com.oztechan.adtrack.data.admob.model.DateRange
import com.oztechan.adtrack.domain.model.Period
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Builds AdMob [DateRange]s for the current and previous occurrence of a [Period], anchored to the
 * account's reporting time zone (so "today" matches what AdMob shows).
 */
class PeriodCalculator {

    fun currentRange(period: Period, timeZoneId: String): DateRange {
        val today = today(timeZoneId)
        return when (period) {
            Period.TODAY -> DateRange(today.toApiDate(), today.toApiDate())
            Period.LAST_30_DAYS -> lastNDays(today, DAYS_IN_MONTH)
            Period.LAST_90_DAYS -> lastNDays(today, DAYS_IN_QUARTER)
            Period.LAST_365_DAYS -> lastNDays(today, DAYS_IN_YEAR)
            Period.LIFETIME -> DateRange(LIFETIME_START.toApiDate(), today.toApiDate())
        }
    }

    /**
     * The immediately preceding comparable range, used for the period-over-period delta.
     * Null for [Period.LIFETIME], which has no meaningful "previous" period.
     */
    fun previousRange(period: Period, timeZoneId: String): DateRange? {
        val today = today(timeZoneId)
        return when (period) {
            Period.TODAY -> today.minus(DatePeriod(days = 1)).let { DateRange(it.toApiDate(), it.toApiDate()) }
            Period.LAST_30_DAYS -> previousNDays(today, DAYS_IN_MONTH)
            Period.LAST_90_DAYS -> previousNDays(today, DAYS_IN_QUARTER)
            Period.LAST_365_DAYS -> previousNDays(today, DAYS_IN_YEAR)
            Period.LIFETIME -> null
        }
    }

    // [today - (n-1) .. today] — a rolling window ending today (always n days).
    private fun lastNDays(today: LocalDate, n: Int): DateRange =
        DateRange(today.minus(DatePeriod(days = n - 1)).toApiDate(), today.toApiDate())

    // The n days immediately before the current rolling window.
    private fun previousNDays(today: LocalDate, n: Int): DateRange = DateRange(
        today.minus(DatePeriod(days = 2 * n - 1)).toApiDate(),
        today.minus(DatePeriod(days = n)).toApiDate()
    )

    private fun today(timeZoneId: String): LocalDate =
        Clock.System.todayIn(runCatching { TimeZone.of(timeZoneId) }.getOrDefault(TimeZone.UTC))

    @Suppress("DEPRECATION")
    private fun LocalDate.toApiDate() = ApiDate(year = year, month = monthNumber, day = dayOfMonth)

    companion object {
        private const val DAYS_IN_MONTH = 30
        private const val DAYS_IN_QUARTER = 90
        private const val DAYS_IN_YEAR = 365

        // AdMob predates this; earlier dates simply return no rows. Captures an account's full history.
        private val LIFETIME_START = LocalDate(2010, 1, 1)
    }
}
