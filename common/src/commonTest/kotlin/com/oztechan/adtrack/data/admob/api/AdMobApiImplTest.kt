/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.admob.api

import com.oztechan.adtrack.data.admob.model.ApiDate
import com.oztechan.adtrack.data.admob.model.DateRange
import com.oztechan.adtrack.data.admob.model.NetworkReportSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class AdMobApiImplTest {

    private fun api(responseJson: String, capture: ((String) -> Unit)? = null): AdMobApiImpl {
        val engine = MockEngine { request ->
            capture?.invoke(request.url.toString())
            respond(
                content = responseJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        return AdMobApiImpl(client)
    }

    @Test
    fun getAccounts_parses_account_list() = runTest {
        val json = """
            {"account":[{"name":"accounts/pub-9","publisherId":"pub-9",
            "currencyCode":"EUR","reportingTimeZone":"Europe/Berlin"}]}
        """.trimIndent()

        val accounts = api(json).getAccounts()

        assertEquals(1, accounts.size)
        assertEquals("pub-9", accounts.first().publisherId)
        assertEquals("EUR", accounts.first().currencyCode)
        assertEquals("Europe/Berlin", accounts.first().reportingTimeZone)
    }

    @Test
    fun generateNetworkReport_returns_only_data_rows() = runTest {
        val json = """
            [
              {"header":{"dateRange":{"startDate":{"year":2026,"month":1,"day":1},
                "endDate":{"year":2026,"month":1,"day":2}}}},
              {"row":{"dimensionValues":{"DATE":{"value":"20260101"}},
                "metricValues":{"ESTIMATED_EARNINGS":{"microsValue":"1500000"}}}},
              {"row":{"dimensionValues":{"DATE":{"value":"20260102"}},
                "metricValues":{"ESTIMATED_EARNINGS":{"microsValue":"2500000"}}}},
              {"footer":{"matchingRowCount":"2"}}
            ]
        """.trimIndent()

        var calledUrl = ""
        val rows = api(json) { calledUrl = it }.generateNetworkReport(
            publisherId = "pub-9",
            spec = NetworkReportSpec(
                dateRange = DateRange(ApiDate(2026, 1, 1), ApiDate(2026, 1, 2)),
                dimensions = listOf("DATE"),
                metrics = listOf("ESTIMATED_EARNINGS")
            )
        )

        assertTrue(calledUrl.endsWith("/accounts/pub-9/networkReport:generate"))
        assertEquals(2, rows.size)
        assertEquals("1500000", rows.first().metricValues["ESTIMATED_EARNINGS"]?.microsValue)
    }

    @Test
    fun blank_publisher_id_fails_without_calling_network() = runTest {
        var called = false
        val service = api("[]") { called = true }
        assertFails {
            service.generateNetworkReport(
                publisherId = "",
                spec = NetworkReportSpec(
                    dateRange = DateRange(ApiDate(2026, 1, 1), ApiDate(2026, 1, 1)),
                    dimensions = listOf("DATE"),
                    metrics = listOf("ESTIMATED_EARNINGS")
                )
            )
        }
        assertTrue(!called, "Network must not be hit for a blank publisher id")
    }
}
