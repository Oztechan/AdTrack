/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.fakes

import com.oztechan.adtrack.data.admob.api.AdMobApi
import com.oztechan.adtrack.data.admob.model.AdMobAccount
import com.oztechan.adtrack.data.admob.model.NetworkReportSpec
import com.oztechan.adtrack.data.admob.model.ReportRow
import com.oztechan.adtrack.data.auth.AuthService
import com.oztechan.adtrack.data.auth.browser.AuthBrowserLauncher
import com.oztechan.adtrack.data.auth.model.TokenResponse
import com.oztechan.adtrack.domain.model.AppRevenue
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.domain.model.RevenueSummary
import com.oztechan.adtrack.domain.repository.AuthRepository
import com.oztechan.adtrack.domain.repository.RevenueRepository
import io.ktor.http.Url
import kotlinx.coroutines.delay

fun fakeAccount(
    publisherId: String = "pub-123",
    currencyCode: String = "USD",
    reportingTimeZone: String = "UTC"
) = AdMobAccount(
    name = "accounts/$publisherId",
    publisherId = publisherId,
    currencyCode = currencyCode,
    reportingTimeZone = reportingTimeZone
)

fun fakeSummary(period: Period = Period.TODAY) = RevenueSummary(
    period = period,
    currencyCode = "USD",
    earnings = 12.5,
    impressions = 100,
    clicks = 5,
    previousEarnings = 10.0,
    deltaPercent = 25.0
)

class FakeAuthService(
    var authUrlBuilder: (String, String) -> String = { challenge, state ->
        "https://accounts.google.com/o/oauth2/v2/auth?code_challenge=$challenge&state=$state"
    },
    var exchangeResponse: TokenResponse = TokenResponse("access", 3600, "refresh"),
    var refreshResponse: TokenResponse = TokenResponse("access2", 3600, "refresh2")
) : AuthService {
    override val redirectUri: String = "com.oztechan.adtrack:/oauth2redirect"
    var lastState: String? = null
    var exchangedCode: String? = null
    var exchangedVerifier: String? = null
    var refreshedToken: String? = null

    override fun buildAuthorizationUrl(codeChallenge: String, state: String): String {
        lastState = state
        return authUrlBuilder(codeChallenge, state)
    }

    override suspend fun exchangeCode(code: String, codeVerifier: String): TokenResponse {
        exchangedCode = code
        exchangedVerifier = codeVerifier
        return exchangeResponse
    }

    override suspend fun refresh(refreshToken: String): TokenResponse {
        refreshedToken = refreshToken
        return refreshResponse
    }
}

/** Echoes the `state` from the auth URL back in the callback, mimicking Google's redirect. */
class FakeAuthBrowserLauncher(
    var code: String? = "auth_code",
    var error: String? = null,
    var overrideState: String? = null,
    var throwable: Throwable? = null
) : AuthBrowserLauncher {
    var lastAuthUrl: String? = null

    override suspend fun authenticate(authUrl: String, callbackScheme: String): String {
        lastAuthUrl = authUrl
        throwable?.let { throw it }
        val state = overrideState ?: Url(authUrl).parameters["state"].orEmpty()
        return buildString {
            append("$callbackScheme:/oauth2redirect?")
            error?.let { append("error=$it&") }
            code?.let { append("code=$it&") }
            append("state=$state")
        }
    }
}

class FakeAdMobApi(
    var accounts: List<AdMobAccount> = listOf(fakeAccount()),
    var reportResponder: (String, NetworkReportSpec) -> List<ReportRow> = { _, _ -> emptyList() }
) : AdMobApi {
    var getAccountsCallCount = 0
    var reportCallCount = 0
    val specs = mutableListOf<NetworkReportSpec>()

    override suspend fun getAccounts(): List<AdMobAccount> {
        getAccountsCallCount++
        return accounts
    }

    override suspend fun generateNetworkReport(
        publisherId: String,
        spec: NetworkReportSpec
    ): List<ReportRow> {
        reportCallCount++
        specs.add(spec)
        return reportResponder(publisherId, spec)
    }
}

class FakeAuthRepository(
    var signedIn: Boolean = false,
    var signInResult: Result<Unit> = Result.success(Unit)
) : AuthRepository {
    var signOutCalled = false
    override fun isSignedIn(): Boolean = signedIn
    override suspend fun signIn(): Result<Unit> = signInResult
    override fun signOut() {
        signOutCalled = true
        signedIn = false
    }
}

class FakeRevenueRepository(
    var account: AdMobAccount = fakeAccount(),
    var summary: RevenueSummary = fakeSummary(),
    var apps: List<AppRevenue> = listOf(AppRevenue("app1", "App One", 9.0, 80, 4)),
    var series: List<RevenuePoint> = emptyList(),
    var error: Throwable? = null
) : RevenueRepository {
    var invalidateCalled = false
    var yesterdaySummaryCalled = false
    var lastAppSeriesId: String? = null
    var summaryDelayMillis: (Period) -> Long = { 0 }

    override suspend fun getAccount(): AdMobAccount = error?.let { throw it } ?: account
    override suspend fun getSummary(period: Period): RevenueSummary {
        delay(summaryDelayMillis(period))
        return error?.let { throw it } ?: summary.copy(period = period)
    }

    override suspend fun getYesterdaySummary(): RevenueSummary {
        yesterdaySummaryCalled = true
        return error?.let { throw it } ?: summary.copy(previousEarnings = null, deltaPercent = null)
    }

    override suspend fun getAppBreakdown(period: Period): List<AppRevenue> =
        error?.let { throw it } ?: apps

    override suspend fun getRevenueSeries(period: Period): List<RevenuePoint> =
        error?.let { throw it } ?: series

    override suspend fun getAppRevenueSeries(period: Period, appId: String): List<RevenuePoint> {
        lastAppSeriesId = appId
        return error?.let { throw it } ?: series
    }

    override fun invalidate() {
        invalidateCalled = true
    }
}
