/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.util

/** Formats [amount] using the platform's locale-aware currency formatter for [currencyCode]. */
expect fun formatCurrency(amount: Double, currencyCode: String): String
