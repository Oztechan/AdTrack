/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object SignInRoute

@Serializable
object DashboardRoute

@Serializable
data class AppDetailRoute(
    val appId: String,
    val appName: String,
    val period: String
)

@Serializable
object SettingsRoute
