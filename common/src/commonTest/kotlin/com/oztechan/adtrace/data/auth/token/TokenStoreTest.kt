/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth.token

import com.oztechan.adtrace.core.storage.SecureStorage
import com.oztechan.adtrace.data.auth.model.TokenResponse
import com.oztechan.adtrace.fakes.FakeAuthService
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenStoreTest {

    private fun store(authService: FakeAuthService = FakeAuthService()) =
        TokenStore(SecureStorage(MapSettings()), authService)

    @Test
    fun not_signed_in_without_a_refresh_token() {
        assertFalse(store().isSignedIn())
    }

    @Test
    fun saving_tokens_persists_refresh_token() {
        val store = store()
        store.saveTokens(TokenResponse(accessToken = "a", expiresIn = 3600, refreshToken = "r"))
        assertTrue(store.isSignedIn())
    }

    @Test
    fun valid_access_token_returns_cached_value_before_expiry() = runTest {
        val store = store()
        store.saveTokens(TokenResponse(accessToken = "fresh", expiresIn = 3600, refreshToken = "r"))
        assertEquals("fresh", store.validAccessToken())
    }

    @Test
    fun expired_access_token_triggers_refresh() = runTest {
        val auth = FakeAuthService(
            refreshResponse = TokenResponse(accessToken = "refreshed", expiresIn = 3600, refreshToken = "r2")
        )
        val store = store(auth)
        // expiresIn = 0 => immediately within the expiry skew, so a refresh is forced.
        store.saveTokens(TokenResponse(accessToken = "old", expiresIn = 0, refreshToken = "r1"))

        assertEquals("refreshed", store.validAccessToken())
        assertEquals("r1", auth.refreshedToken)
    }

    @Test
    fun refresh_returns_null_when_no_refresh_token() = runTest {
        assertNull(store().validAccessToken())
    }

    @Test
    fun clear_signs_out() {
        val store = store()
        store.saveTokens(TokenResponse(accessToken = "a", expiresIn = 3600, refreshToken = "r"))
        store.clear()
        assertFalse(store.isSignedIn())
    }
}
