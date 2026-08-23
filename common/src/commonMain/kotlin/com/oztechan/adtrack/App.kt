/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.oztechan.adtrack.ads.interstitial.InterstitialManager
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.repository.AuthRepository
import com.oztechan.adtrack.ui.feature.appdetail.AppDetailScreen
import com.oztechan.adtrack.ui.feature.dashboard.DashboardScreen
import com.oztechan.adtrack.ui.feature.settings.SettingsScreen
import com.oztechan.adtrack.ui.feature.signin.SignInScreen
import com.oztechan.adtrack.ui.navigation.AppDetailRoute
import com.oztechan.adtrack.ui.navigation.DashboardRoute
import com.oztechan.adtrack.ui.navigation.SettingsRoute
import com.oztechan.adtrack.ui.navigation.SignInRoute
import com.oztechan.adtrack.ui.theme.AdTrackTheme
import org.koin.compose.koinInject

@Composable
fun AdTrackApp() {
    AdTrackTheme {
        val authRepository = koinInject<AuthRepository>()
        val interstitialManager = koinInject<InterstitialManager>()
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
                // Preload from the home screen so an interstitial is ready by the time the user
                // returns from a detail/settings transition (subject to the manager's policy).
                LaunchedEffect(Unit) { interstitialManager.preload() }
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
                    onNavigateBack = {
                        interstitialManager.onTransition()
                        navController.popBackStack()
                    }
                )
            }
            composable<SettingsRoute> {
                SettingsScreen(
                    onNavigateToSignIn = {
                        navController.navigate(SignInRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        interstitialManager.onTransition()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
