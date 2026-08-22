/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oztechan.adtrack.ads.banner.BannerAd
import com.oztechan.adtrack.ads.rewarded.RewardedAdState
import com.oztechan.adtrack.ui.theme.AdTrackTheme
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateToSignIn: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SettingsEffect.NavigateToSignIn -> onNavigateToSignIn()
                SettingsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    SettingsScreenContent(
        state = state,
        onSignOutClick = viewModel.event::onSignOutClick,
        onBackClick = viewModel.event::onBackClick,
        onWatchRewardedAd = viewModel.event::onWatchRewardedAd,
        bottomBar = { BannerAd() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    state: SettingsState,
    onSignOutClick: () -> Unit,
    onBackClick: () -> Unit,
    onWatchRewardedAd: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 48.dp)
                )
            } else {
                InfoRow("Publisher ID", state.publisherId)
                InfoRow("Currency", state.currencyCode)
                InfoRow("Reporting time zone", state.reportingTimeZone)
                state.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
            }

            RemoveAdsSection(
                isPremium = state.isPremium,
                rewardedAdState = state.rewardedAdState,
                onWatchRewardedAd = onWatchRewardedAd,
                modifier = Modifier.padding(top = 24.dp)
            )

            Button(
                onClick = onSignOutClick,
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Sign out")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Value-exchange card: watch a rewarded ad to earn (or extend) a 2-day ad-free window. */
@Composable
private fun RemoveAdsSection(
    isPremium: Boolean,
    rewardedAdState: RewardedAdState,
    onWatchRewardedAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Remove ads", style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (isPremium) {
                    "Ads are off for now — thanks for watching. Watch again to add another 2 days."
                } else {
                    "Watch a short ad to go ad-free for 2 days."
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            val loading = rewardedAdState == RewardedAdState.LOADING
            OutlinedButton(
                onClick = onWatchRewardedAd,
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isPremium) "Watch to add 2 more days" else "Watch ad — 2 days ad-free")
                }
            }

            if (rewardedAdState == RewardedAdState.FAILED) {
                Text(
                    text = "Couldn't load the ad. Please try again.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsLoadedPreview() {
    AdTrackTheme {
        SettingsScreenContent(
            state = SettingsState(
                isLoading = false,
                publisherId = "pub-1234567890123456",
                currencyCode = "USD",
                reportingTimeZone = "America/Los_Angeles"
            ),
            onSignOutClick = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun SettingsLoadingPreview() {
    AdTrackTheme {
        SettingsScreenContent(
            state = SettingsState(isLoading = true),
            onSignOutClick = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun SettingsPremiumPreview() {
    AdTrackTheme {
        SettingsScreenContent(
            state = SettingsState(
                isLoading = false,
                publisherId = "pub-1234567890123456",
                currencyCode = "USD",
                reportingTimeZone = "America/Los_Angeles",
                isPremium = true
            ),
            onSignOutClick = {},
            onBackClick = {}
        )
    }
}
