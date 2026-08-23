/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.network.di

import android.app.Application
import com.oztechan.adtrack.data.auth.token.TokenProvider
import com.oztechan.adtrack.di.FakeTokenProvider
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class NetworkModuleTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_json_and_both_http_clients() {
        val koin = startKoin {
            modules(
                networkModule,
                module { single<TokenProvider> { FakeTokenProvider() } }
            )
        }.koin

        assertNotNull(koin.getOrNull<Json>())
        assertNotNull(koin.getOrNull<HttpClient>(PLAIN_CLIENT))
        assertNotNull(koin.getOrNull<HttpClient>(AUTH_CLIENT))
    }
}
