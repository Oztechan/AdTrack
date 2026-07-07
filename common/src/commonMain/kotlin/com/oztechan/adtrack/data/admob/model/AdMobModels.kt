/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.admob.model

import kotlinx.serialization.Serializable

// region accounts.list
@Serializable
data class AccountsResponse(
    val account: List<AdMobAccount> = emptyList()
)

@Serializable
data class AdMobAccount(
    val name: String = "",
    val publisherId: String = "",
    val currencyCode: String = "USD",
    val reportingTimeZone: String = "America/Los_Angeles"
)
// endregion

// region networkReport:generate request
@Serializable
data class GenerateNetworkReportRequest(
    val reportSpec: NetworkReportSpec
)

@Serializable
data class NetworkReportSpec(
    val dateRange: DateRange,
    val dimensions: List<String>,
    val metrics: List<String>,
    val localizationSettings: LocalizationSettings? = null,
    val dimensionFilters: List<DimensionFilter>? = null
)

@Serializable
data class DimensionFilter(
    val dimension: String,
    val matchesAny: StringList
)

@Serializable
data class StringList(
    val values: List<String>
)

@Serializable
data class DateRange(
    val startDate: ApiDate,
    val endDate: ApiDate
)

@Serializable
data class ApiDate(
    val year: Int,
    val month: Int,
    val day: Int
)

@Serializable
data class LocalizationSettings(
    val currencyCode: String? = null,
    val languageCode: String? = null
)
// endregion

// region networkReport:generate response (a JSON array of header/row/footer objects)
@Serializable
data class ReportLine(
    val header: ReportHeader? = null,
    val row: ReportRow? = null,
    val footer: ReportFooter? = null
)

@Serializable
data class ReportHeader(
    val dateRange: DateRange? = null
)

@Serializable
data class ReportRow(
    val dimensionValues: Map<String, DimensionValue> = emptyMap(),
    val metricValues: Map<String, MetricValue> = emptyMap()
)

@Serializable
data class ReportFooter(
    val matchingRowCount: String? = null
)

@Serializable
data class DimensionValue(
    val value: String? = null,
    val displayLabel: String? = null
)

// Google JSON encodes int64/uint64 as strings, so micros/integer values arrive as String.
@Serializable
data class MetricValue(
    val microsValue: String? = null,
    val integerValue: String? = null,
    val doubleValue: Double? = null
)
// endregion
