/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth

import com.oztechan.adtrace.config.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthServiceImplTest {

    private val tokenJson = """{"access_token":"abc","expires_in":3600,"refresh_token":"r1"}"""

    private fun service(
        capture: ((url: String, body: String) -> Unit)? = null
    ): AuthServiceImpl {
        val engine = MockEngine { request ->
            val body = (request.body as OutgoingContent.ByteArrayContent).bytes().decodeToString()
            capture?.invoke(request.url.toString(), body)
            respond(
                content = tokenJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        return AuthServiceImpl(client)
    }

    @Test
    fun authorization_url_contains_pkce_and_offline_params() {
        val url = Url(service().buildAuthorizationUrl(codeChallenge = "CHAL", state = "STATE"))
        val params = url.parameters

        assertEquals(BuildKonfig.GOOGLE_OAUTH_CLIENT_ID, params["client_id"])
        assertEquals("https://www.googleapis.com/auth/admob.readonly", params["scope"])
        assertEquals("CHAL", params["code_challenge"])
        assertEquals("S256", params["code_challenge_method"])
        assertEquals("STATE", params["state"])
        assertEquals("offline", params["access_type"])
        assertEquals("consent", params["prompt"])
        assertEquals("code", params["response_type"])
    }

    @Test
    fun redirect_uri_uses_single_slash_reverse_dns() {
        assertTrue(service().redirectUri.endsWith(":/oauth2redirect"))
        assertTrue(":/oauth2redirect" in service().redirectUri)
        assertTrue("://" !in service().redirectUri)
    }

    @Test
    fun exchangeCode_posts_authorization_code_grant() = runTest {
        var capturedUrl = ""
        var capturedBody = ""
        val response = service { url, body ->
            capturedUrl = url
            capturedBody = body
        }.exchangeCode(code = "THECODE", codeVerifier = "VER")

        assertEquals("https://oauth2.googleapis.com/token", capturedUrl)
        assertTrue("grant_type=authorization_code" in capturedBody)
        assertTrue("code=THECODE" in capturedBody)
        assertTrue("code_verifier=VER" in capturedBody)
        assertTrue("client_id=" in capturedBody)
        assertEquals("abc", response.accessToken)
        assertEquals("r1", response.refreshToken)
    }

    @Test
    fun refresh_posts_refresh_token_grant() = runTest {
        var capturedBody = ""
        service { _, body -> capturedBody = body }.refresh(refreshToken = "MYREFRESH")

        assertTrue("grant_type=refresh_token" in capturedBody)
        assertTrue("refresh_token=MYREFRESH" in capturedBody)
    }
}
