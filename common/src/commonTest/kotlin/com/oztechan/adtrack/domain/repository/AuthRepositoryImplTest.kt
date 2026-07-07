/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain.repository

import com.oztechan.adtrack.core.storage.SecureStorage
import com.oztechan.adtrack.data.auth.browser.AuthCancelledException
import com.oztechan.adtrack.data.auth.model.TokenResponse
import com.oztechan.adtrack.data.auth.token.TokenStore
import com.oztechan.adtrack.fakes.FakeAuthBrowserLauncher
import com.oztechan.adtrack.fakes.FakeAuthService
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthRepositoryImplTest {

    private fun build(
        launcher: FakeAuthBrowserLauncher = FakeAuthBrowserLauncher(),
        authService: FakeAuthService = FakeAuthService(
            exchangeResponse = TokenResponse(accessToken = "a", expiresIn = 3600, refreshToken = "r")
        )
    ): Triple<AuthRepositoryImpl, TokenStore, FakeAuthService> {
        val tokenStore = TokenStore(SecureStorage(MapSettings()), authService)
        val repo = AuthRepositoryImpl(authService, launcher, tokenStore)
        return Triple(repo, tokenStore, authService)
    }

    @Test
    fun successful_signIn_exchanges_code_and_persists_tokens() = runTest {
        val (repo, store, auth) = build(FakeAuthBrowserLauncher(code = "the_code"))

        val result = repo.signIn()

        assertTrue(result.isSuccess)
        assertTrue(store.isSignedIn())
        assertEquals("the_code", auth.exchangedCode)
    }

    @Test
    fun state_mismatch_fails_signIn() = runTest {
        val (repo, store, _) = build(FakeAuthBrowserLauncher(overrideState = "tampered"))

        val result = repo.signIn()

        assertTrue(result.isFailure)
        assertFalse(store.isSignedIn())
    }

    @Test
    fun error_in_callback_fails_signIn() = runTest {
        val (repo, _, _) = build(FakeAuthBrowserLauncher(error = "access_denied", code = null))
        assertTrue(repo.signIn().isFailure)
    }

    @Test
    fun missing_code_fails_signIn() = runTest {
        val (repo, _, _) = build(FakeAuthBrowserLauncher(code = null))
        assertTrue(repo.signIn().isFailure)
    }

    @Test
    fun cancellation_results_in_failure() = runTest {
        val (repo, _, _) = build(FakeAuthBrowserLauncher(throwable = AuthCancelledException()))
        val result = repo.signIn()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AuthCancelledException)
    }

    @Test
    fun signOut_clears_tokens() = runTest {
        val (repo, store, _) = build()
        repo.signIn()
        assertTrue(store.isSignedIn())

        repo.signOut()
        assertFalse(store.isSignedIn())
    }
}
