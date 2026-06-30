/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.core.network.di

import com.oztechan.adtrace.data.auth.token.TokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val TIME_OUT: Long = 15_000

/** Qualifier for the unauthenticated client used by the OAuth token endpoint. */
val PLAIN_CLIENT = named("plainClient")

/** Qualifier for the bearer-authenticated client used by the AdMob API. */
val AUTH_CLIENT = named("authClient")

val networkModule = module {
    single {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    single(PLAIN_CLIENT) {
        HttpClient {
            setupHttpClientConfig(get())
        }
    }

    single(AUTH_CLIENT) {
        val tokenProvider = get<TokenProvider>()
        HttpClient {
            setupHttpClientConfig(get())
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenProvider.validAccessToken()?.let { BearerTokens(it, null) }
                    }
                    refreshTokens {
                        tokenProvider.refreshAccessToken()?.let { BearerTokens(it, null) }
                    }
                }
            }
        }
    }
}

internal fun HttpClientConfig<*>.setupHttpClientConfig(json: Json) {
    install(ContentNegotiation) {
        json(json = json, contentType = ContentType.Any)
    }
    install(HttpTimeout) {
        connectTimeoutMillis = TIME_OUT
        socketTimeoutMillis = TIME_OUT
        requestTimeoutMillis = TIME_OUT
    }
    install(Logging) {
        level = LogLevel.INFO
    }
}
