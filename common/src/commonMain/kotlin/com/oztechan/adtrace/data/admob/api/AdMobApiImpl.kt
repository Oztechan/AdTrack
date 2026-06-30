/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.admob.api

import com.oztechan.adtrace.core.coroutine.ioDispatcher
import com.oztechan.adtrace.core.network.base.BaseNetworkService
import com.oztechan.adtrace.data.admob.model.AccountsResponse
import com.oztechan.adtrace.data.admob.model.AdMobAccount
import com.oztechan.adtrace.data.admob.model.GenerateNetworkReportRequest
import com.oztechan.adtrace.data.admob.model.NetworkReportSpec
import com.oztechan.adtrace.data.admob.model.ReportLine
import com.oztechan.adtrace.data.admob.model.ReportRow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val BASE_URL = "https://admob.googleapis.com/v1"

class AdMobApiImpl(
    private val httpClient: HttpClient
) : BaseNetworkService(ioDispatcher), AdMobApi {

    override suspend fun getAccounts(): List<AdMobAccount> = apiRequest {
        httpClient.get("$BASE_URL/accounts")
            .body<AccountsResponse>()
            .account
    }

    override suspend fun generateNetworkReport(
        publisherId: String,
        spec: NetworkReportSpec
    ): List<ReportRow> = apiRequest {
        withEmptyParameterCheck(publisherId)
        httpClient.post("$BASE_URL/accounts/$publisherId/networkReport:generate") {
            contentType(ContentType.Application.Json)
            setBody(GenerateNetworkReportRequest(reportSpec = spec))
        }
            .body<List<ReportLine>>()
            .mapNotNull { it.row }
    }
}
