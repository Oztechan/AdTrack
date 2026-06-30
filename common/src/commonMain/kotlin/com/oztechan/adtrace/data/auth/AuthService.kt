/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth

import com.oztechan.adtrace.data.auth.model.TokenResponse

interface AuthService {

    /** The redirect URI registered with the Google OAuth client, derived from the configured scheme. */
    val redirectUri: String

    /** Builds the Google authorization-code + PKCE URL to open in the system browser. */
    fun buildAuthorizationUrl(codeChallenge: String, state: String): String

    /** Exchanges the authorization [code] for tokens (no client secret — native PKCE client). */
    suspend fun exchangeCode(code: String, codeVerifier: String): TokenResponse

    /** Exchanges a [refreshToken] for a fresh access token. */
    suspend fun refresh(refreshToken: String): TokenResponse
}
