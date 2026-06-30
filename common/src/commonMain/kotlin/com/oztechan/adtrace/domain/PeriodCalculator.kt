/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.domain

import com.oztechan.adtrace.data.admob.model.ApiDate
import com.oztechan.adtrace.data.admob.model.DateRange
import com.oztechan.adtrace.domain.model.Period
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
            Period.LAST_7_DAYS -> lastNDays(today, DAYS_IN_WEEK)
            Period.THIS_MONTH -> DateRange(LocalDate(today.year, today.month, 1).toApiDate(), today.toApiDate())
            Period.LAST_90_DAYS -> lastNDays(today, DAYS_IN_QUARTER)
        }
    }

    /** The immediately preceding comparable range, used for the period-over-period delta. */
    fun previousRange(period: Period, timeZoneId: String): DateRange {
        val today = today(timeZoneId)
        return when (period) {
            Period.TODAY -> today.minus(DatePeriod(days = 1)).let { DateRange(it.toApiDate(), it.toApiDate()) }
            Period.LAST_7_DAYS -> previousNDays(today, DAYS_IN_WEEK)
            Period.THIS_MONTH -> {
                val prevEnd = LocalDate(today.year, today.month, 1).minus(DatePeriod(days = 1))
                DateRange(LocalDate(prevEnd.year, prevEnd.month, 1).toApiDate(), prevEnd.toApiDate())
            }
            Period.LAST_90_DAYS -> previousNDays(today, DAYS_IN_QUARTER)
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
        private const val DAYS_IN_WEEK = 7
        private const val DAYS_IN_QUARTER = 90
    }
}
