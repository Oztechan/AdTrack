/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oztechan.adtrack.ui.theme.AdTrackTheme
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: SignInViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SignInEffect.NavigateToDashboard -> onNavigateToDashboard()
                is SignInEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    SignInContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onSignInClick = viewModel.event::onSignInClick
    )
}

@Composable
private fun SignInContent(
    state: SignInState,
    snackbarHostState: SnackbarHostState,
    onSignInClick: () -> Unit
) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "AdTrack", style = MaterialTheme.typography.displaySmall)
            Text(
                text = "Track your AdMob revenue",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
            )
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                Button(onClick = onSignInClick) {
                    Text("Sign in with Google")
                }
            }
        }
    }
}

@Preview
@Composable
private fun SignInContentPreview() {
    AdTrackTheme {
        SignInContent(SignInState(), remember { SnackbarHostState() }, {})
    }
}

@Preview
@Composable
private fun SignInLoadingPreview() {
    AdTrackTheme {
        SignInContent(SignInState(isLoading = true), remember { SnackbarHostState() }, {})
    }
}
