/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.oztechan.adtrace.domain.model.Period
import com.oztechan.adtrace.domain.repository.AuthRepository
import com.oztechan.adtrace.ui.feature.appdetail.AppDetailScreen
import com.oztechan.adtrace.ui.feature.dashboard.DashboardScreen
import com.oztechan.adtrace.ui.feature.settings.SettingsScreen
import com.oztechan.adtrace.ui.feature.signin.SignInScreen
import com.oztechan.adtrace.ui.navigation.AppDetailRoute
import com.oztechan.adtrace.ui.navigation.DashboardRoute
import com.oztechan.adtrace.ui.navigation.SettingsRoute
import com.oztechan.adtrace.ui.navigation.SignInRoute
import com.oztechan.adtrace.ui.theme.AdTraceTheme
import org.koin.compose.koinInject

@Composable
fun AdTraceApp() {
    AdTraceTheme {
        val authRepository = koinInject<AuthRepository>()
        val navController = rememberNavController()
        val startDestination = remember {
            if (authRepository.isSignedIn()) DashboardRoute else SignInRoute
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable<SignInRoute> {
                SignInScreen(
                    onNavigateToDashboard = {
                        navController.navigate(DashboardRoute) {
                            popUpTo(SignInRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<DashboardRoute> {
                DashboardScreen(
                    onNavigateToAppDetail = { appId, appName, period ->
                        navController.navigate(AppDetailRoute(appId, appName, period.name))
                    },
                    onNavigateToSettings = { navController.navigate(SettingsRoute) }
                )
            }
            composable<AppDetailRoute> { entry ->
                val route = entry.toRoute<AppDetailRoute>()
                AppDetailScreen(
                    appId = route.appId,
                    appName = route.appName,
                    period = Period.valueOf(route.period),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<SettingsRoute> {
                SettingsScreen(
                    onNavigateToSignIn = {
                        navController.navigate(SignInRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
