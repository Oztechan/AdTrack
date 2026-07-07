/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.domain.repository

import com.oztechan.adtrack.config.BuildKonfig
import com.oztechan.adtrack.core.crypto.Pkce
import com.oztechan.adtrack.data.auth.AuthService
import com.oztechan.adtrack.data.auth.browser.AuthBrowserLauncher
import com.oztechan.adtrack.data.auth.browser.AuthException
import com.oztechan.adtrack.data.auth.token.TokenStore
import io.ktor.http.decodeURLQueryComponent

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val browserLauncher: AuthBrowserLauncher,
    private val tokenStore: TokenStore
) : AuthRepository {

    override fun isSignedIn(): Boolean = tokenStore.isSignedIn()

    override suspend fun signIn(): Result<Unit> = runCatching {
        val codeVerifier = Pkce.createCodeVerifier()
        val codeChallenge = Pkce.codeChallengeFor(codeVerifier)
        val state = Pkce.createState()

        val authUrl = authService.buildAuthorizationUrl(codeChallenge, state)
        val callbackUrl = browserLauncher.authenticate(authUrl, BuildKonfig.OAUTH_REDIRECT_SCHEME)

        val params = parseQuery(callbackUrl)
        params["error"]?.let { throw AuthException("Authorization denied: $it") }
        if (params["state"] != state) throw AuthException("State mismatch — possible CSRF")
        val code = params["code"] ?: throw AuthException("Missing authorization code")

        val tokens = authService.exchangeCode(code, codeVerifier)
        tokenStore.saveTokens(tokens)
    }

    override fun signOut() = tokenStore.clear()

    // The redirect uses an opaque custom scheme (com.oztechan.adtrack:/oauth2redirect?...),
    // which generic URL parsers handle inconsistently, so parse the query string directly.
    private fun parseQuery(url: String): Map<String, String> = url
        .substringAfter('?', "")
        .split('&')
        .filter { it.isNotEmpty() }
        .associate { pair ->
            val key = pair.substringBefore('=')
            val value = pair.substringAfter('=', "")
            key.decodeURLQueryComponent() to value.decodeURLQueryComponent(plusIsSpace = true)
        }
}
