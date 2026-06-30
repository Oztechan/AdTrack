/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth

import com.oztechan.adtrace.config.BuildKonfig
import com.oztechan.adtrace.core.coroutine.ioDispatcher
import com.oztechan.adtrace.core.network.base.BaseNetworkService
import com.oztechan.adtrace.data.auth.model.TokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder

private const val AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth"
private const val TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token"
private const val ADMOB_SCOPE = "https://www.googleapis.com/auth/admob.readonly"

class AuthServiceImpl(
    private val httpClient: HttpClient
) : BaseNetworkService(ioDispatcher), AuthService {

    // Google iOS-client reverse-DNS redirect: single-slash path style, NOT "://host".
    override val redirectUri: String = "${BuildKonfig.OAUTH_REDIRECT_SCHEME}:/oauth2redirect"

    override fun buildAuthorizationUrl(codeChallenge: String, state: String): String =
        URLBuilder(AUTH_ENDPOINT).apply {
            parameters.append("client_id", BuildKonfig.GOOGLE_OAUTH_CLIENT_ID)
            parameters.append("redirect_uri", redirectUri)
            parameters.append("response_type", "code")
            parameters.append("scope", ADMOB_SCOPE)
            parameters.append("code_challenge", codeChallenge)
            parameters.append("code_challenge_method", "S256")
            parameters.append("state", state)
            parameters.append("access_type", "offline")
            parameters.append("prompt", "consent")
        }.buildString()

    override suspend fun exchangeCode(
        code: String,
        codeVerifier: String
    ): TokenResponse = apiRequest {
        httpClient.submitForm(
            url = TOKEN_ENDPOINT,
            formParameters = Parameters.build {
                append("client_id", BuildKonfig.GOOGLE_OAUTH_CLIENT_ID)
                append("code", code)
                append("code_verifier", codeVerifier)
                append("grant_type", "authorization_code")
                append("redirect_uri", redirectUri)
            }
        ).body()
    }

    override suspend fun refresh(
        refreshToken: String
    ): TokenResponse = apiRequest {
        httpClient.submitForm(
            url = TOKEN_ENDPOINT,
            formParameters = Parameters.build {
                append("client_id", BuildKonfig.GOOGLE_OAUTH_CLIENT_ID)
                append("refresh_token", refreshToken)
                append("grant_type", "refresh_token")
            }
        ).body()
    }
}
