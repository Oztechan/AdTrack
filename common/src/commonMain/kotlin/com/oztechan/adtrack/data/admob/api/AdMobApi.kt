/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.admob.api

import com.oztechan.adtrack.data.admob.model.AdMobAccount
import com.oztechan.adtrack.data.admob.model.NetworkReportSpec
import com.oztechan.adtrack.data.admob.model.ReportRow

interface AdMobApi {
    /** Lists AdMob accounts authorized for the signed-in user. */
    suspend fun getAccounts(): List<AdMobAccount>

    /** Runs a network report and returns only its data rows. */
    suspend fun generateNetworkReport(publisherId: String, spec: NetworkReportSpec): List<ReportRow>

    object Dimension {
        const val DATE = "DATE"
        const val APP = "APP"
        const val COUNTRY = "COUNTRY"
        const val PLATFORM = "PLATFORM"
    }

    object Metric {
        const val ESTIMATED_EARNINGS = "ESTIMATED_EARNINGS"
        const val IMPRESSIONS = "IMPRESSIONS"
        const val CLICKS = "CLICKS"
        const val AD_REQUESTS = "AD_REQUESTS"
    }
}
