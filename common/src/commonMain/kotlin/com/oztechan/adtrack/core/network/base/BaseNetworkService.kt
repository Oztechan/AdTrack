/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.network.base

import co.touchlab.kermit.Logger
import com.oztechan.adtrack.core.network.error.EmptyParameterException
import com.oztechan.adtrack.core.network.error.ModelMappingException
import com.oztechan.adtrack.core.network.error.NetworkException
import com.oztechan.adtrack.core.network.error.TerminationException
import com.oztechan.adtrack.core.network.error.TimeoutException
import com.oztechan.adtrack.core.network.error.UnauthorizedException
import com.oztechan.adtrack.core.network.error.UnknownNetworkException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

open class BaseNetworkService(private val ioDispatcher: CoroutineDispatcher) {
    protected suspend fun <T> apiRequest(
        suspendBlock: suspend () -> T
    ): T = withContext(ioDispatcher) {
        makeRequest {
            suspendBlock.invoke()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun <T> makeRequest(
        suspendBlock: suspend () -> T
    ): T = try {
        suspendBlock.invoke()
    } catch (e: Throwable) {
        when (e) {
            is CancellationException -> TerminationException(e)
            is ConnectTimeoutException -> TimeoutException(e)
            is ClientRequestException ->
                if (e.response.status == HttpStatusCode.Unauthorized) {
                    UnauthorizedException(e)
                } else {
                    NetworkException(e)
                }
            is IOException -> NetworkException(e)
            is SerializationException -> ModelMappingException(e)
            else -> UnknownNetworkException(e)
        }.let {
            Logger.e(it) { it.message.orEmpty() }
            throw it
        }
    }

    protected fun withEmptyParameterCheck(parameter: String) = parameter.ifEmpty {
        EmptyParameterException().let {
            Logger.e(it) { it.message.orEmpty() }
            throw it
        }
    }
}
