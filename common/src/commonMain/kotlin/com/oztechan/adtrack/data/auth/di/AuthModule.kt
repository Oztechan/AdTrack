/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.auth.di

import com.oztechan.adtrack.core.network.di.PLAIN_CLIENT
import com.oztechan.adtrack.core.storage.SecureStorage
import com.oztechan.adtrack.data.auth.AuthService
import com.oztechan.adtrack.data.auth.AuthServiceImpl
import com.oztechan.adtrack.data.auth.token.TokenProvider
import com.oztechan.adtrack.data.auth.token.TokenStore
import com.oztechan.adtrack.domain.repository.AuthRepository
import com.oztechan.adtrack.domain.repository.AuthRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    single { SecureStorage(get()) }

    single<AuthService> { AuthServiceImpl(get(PLAIN_CLIENT)) }

    single { TokenStore(get(), get()) } bind TokenProvider::class

    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
}
