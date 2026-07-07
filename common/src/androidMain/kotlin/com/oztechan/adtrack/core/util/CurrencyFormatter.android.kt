/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.util

import java.text.NumberFormat
import java.util.Currency

@Suppress("TooGenericExceptionCaught", "SwallowedException")
actual fun formatCurrency(amount: Double, currencyCode: String): String = try {
    NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance(currencyCode)
    }.format(amount)
} catch (e: Exception) {
    "$currencyCode ${(amount * 100).toLong() / 100.0}"
}
