package com.viktormykhailiv.kmp.health.sample.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.viktormykhailiv.kmp.health.HealthManager
import com.viktormykhailiv.kmp.health.sample.presentation.HomeScreen
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.addHomeScreen(
    navController: NavHostController,
    healthManager: HealthManager,
) {
    composable<NavDestinations.Root> {
        HomeScreen(
            viewModel = koinInject { parametersOf(healthManager) },
        )
    }
}

fun NavGraphBuilder.addDataTypeScreens(
    navController: NavHostController,
) {
    ScreenMappings.all.forEach { (destination, screenContent) ->
        composable(destination.route) {
            screenContent({ navController.popBackStack() })
        }
    }
}

fun NavGraphBuilder.buildHealthNavGraph(
    navController: NavHostController,
    healthManager: HealthManager,
) {
    addHomeScreen(navController, healthManager)
    addDataTypeScreens(navController)
}