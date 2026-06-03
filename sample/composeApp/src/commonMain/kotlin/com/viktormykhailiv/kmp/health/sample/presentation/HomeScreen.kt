package com.viktormykhailiv.kmp.health.sample.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.viktormykhailiv.kmp.health.sample.navigation.NavDestinations
import com.viktormykhailiv.kmp.health.sample.ui.AppBar
import com.viktormykhailiv.kmp.health.sample.ui.AppButton
import com.viktormykhailiv.kmp.health.sample.ui.HealthColorScheme
import com.viktormykhailiv.kmp.health.sample.ui.HealthManagerCard
import com.viktormykhailiv.kmp.health.sample.ui.HealthTypography
import com.viktormykhailiv.kmp.health.sample.ui.HealthVitalsDashboard
import org.koin.compose.viewmodel.koinViewModel
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val isRefreshing = uiState is HomeUiState.Loading
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onResume()
        }
    }

    MaterialTheme(
        colorScheme = HealthColorScheme,
        typography = HealthTypography,
    ) {
        Scaffold(
            topBar = {
                AppBar(title = "HealthKMP")
            },
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.syncData() },
                state = pullRefreshState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                val readyState = uiState as? HomeUiState.Ready

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    HealthManagerCard(
                        state = uiState,
                        lastSyncLabel = "",
                        onRequestReadPermission = viewModel::requestAuthorization,
                        onRequestFullPermission = if (readyState?.backgroundSyncSupported == true) viewModel::requestAuthorizationWithBackground else null,
                        onRequestBackgroundPermission = if (readyState?.backgroundSyncSupported == true) viewModel::requestBackgroundPermission else null,
                        onRevokePermission = if (readyState?.revokeSupported == true) viewModel::revokeAuthorization else null,
                    )
                    HealthVitalsDashboard(viewModel)
                }
            }
        }
    }
}

@Composable
private fun DataTypeNavigationButtons(onNavigate: (NavDestinations) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val dataTypes = listOf(
            "Blood glucose" to NavDestinations.BloodGlucose,
            "Blood pressure" to NavDestinations.BloodPressure,
            "Body fat" to NavDestinations.BodyFat,
            "Body temperature" to NavDestinations.BodyTemperature,
            "Cycling pedaling cadence" to NavDestinations.CyclingPedalingCadence,
            "Exercise" to NavDestinations.Exercise,
            "Heart rate" to NavDestinations.HeartRate,
            "Height" to NavDestinations.Height,
            "Lean body mass" to NavDestinations.LeanBodyMass,
            "Menstruation flow" to NavDestinations.MenstruationFlow,
            "Menstruation period" to NavDestinations.MenstruationPeriod,
            "Ovulation test" to NavDestinations.OvulationTest,
            "Power" to NavDestinations.Power,
            "Sexual activity" to NavDestinations.SexualActivity,
            "Sleep" to NavDestinations.Sleep,
            "Steps" to NavDestinations.Steps,
            "Weight" to NavDestinations.Weight,
        )

        dataTypes.forEach { (title, destination) ->
            AppButton(
                text = title,
                onClick = { onNavigate(destination) },
            )
        }
    }
}

expect fun getPlatformName(): String