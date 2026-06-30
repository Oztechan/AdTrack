/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.core.network.error

class NetworkException(cause: Throwable) : Throwable(cause)

class UnknownNetworkException(cause: Throwable) : Throwable(cause)

class TimeoutException(cause: Throwable) : Throwable(cause)

class ModelMappingException(cause: Throwable) : Throwable(cause)

class TerminationException(cause: Throwable) : Throwable(cause)

class UnauthorizedException(cause: Throwable) : Throwable(cause)

class EmptyParameterException : Exception("parameter can not be empty")
