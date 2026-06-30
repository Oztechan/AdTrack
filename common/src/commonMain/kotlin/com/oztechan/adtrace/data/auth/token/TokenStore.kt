/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth.token

import com.oztechan.adtrace.core.storage.SecureStorage
import com.oztechan.adtrace.data.auth.AuthService
import com.oztechan.adtrace.data.auth.model.TokenResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

/**
 * Holds the OAuth tokens: the refresh token is persisted in [SecureStorage]; the access token is
 * cached in memory with its expiry. Refresh is [Mutex]-guarded so concurrent 401s don't race on the
 * (potentially rotating) refresh token.
 */
class TokenStore(
    private val secureStorage: SecureStorage,
    private val authService: AuthService
) : TokenProvider {

    private val mutex = Mutex()
    private var cachedAccessToken: String? = null
    private var accessTokenExpiryEpochSeconds: Long = 0L

    val refreshToken: String?
        get() = secureStorage.getString(KEY_REFRESH_TOKEN)

    fun isSignedIn(): Boolean = refreshToken != null

    fun saveTokens(response: TokenResponse) {
        response.refreshToken?.let { secureStorage.putString(KEY_REFRESH_TOKEN, it) }
        cacheAccessToken(response)
    }

    override suspend fun validAccessToken(): String? {
        cachedAccessToken?.let { if (!isAccessTokenExpiring()) return it }
        return refreshAccessToken()
    }

    override suspend fun refreshAccessToken(): String? = mutex.withLock {
        // Another coroutine may have refreshed while we waited for the lock.
        cachedAccessToken?.let { if (!isAccessTokenExpiring()) return it }

        val token = refreshToken ?: return null
        val response = runCatching { authService.refresh(token) }.getOrNull() ?: return null
        saveTokens(response)
        cachedAccessToken
    }

    fun clear() {
        secureStorage.remove(KEY_REFRESH_TOKEN)
        cachedAccessToken = null
        accessTokenExpiryEpochSeconds = 0L
    }

    private fun cacheAccessToken(response: TokenResponse) {
        cachedAccessToken = response.accessToken
        accessTokenExpiryEpochSeconds = Clock.System.now().epochSeconds + response.expiresIn
    }

    private fun isAccessTokenExpiring(): Boolean =
        Clock.System.now().epochSeconds >= accessTokenExpiryEpochSeconds - EXPIRY_SKEW_SECONDS

    companion object {
        private const val KEY_REFRESH_TOKEN = "oauth_refresh_token"
        private const val EXPIRY_SKEW_SECONDS = 60L
    }
}
