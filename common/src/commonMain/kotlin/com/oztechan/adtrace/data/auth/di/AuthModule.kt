/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth.di

import com.oztechan.adtrace.core.network.di.PLAIN_CLIENT
import com.oztechan.adtrace.core.storage.SecureStorage
import com.oztechan.adtrace.data.auth.AuthService
import com.oztechan.adtrace.data.auth.AuthServiceImpl
import com.oztechan.adtrace.data.auth.token.TokenProvider
import com.oztechan.adtrace.data.auth.token.TokenStore
import com.oztechan.adtrace.domain.repository.AuthRepository
import com.oztechan.adtrace.domain.repository.AuthRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    single { SecureStorage(get()) }

    single<AuthService> { AuthServiceImpl(get(PLAIN_CLIENT)) }

    single { TokenStore(get(), get()) } bind TokenProvider::class

    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
}
