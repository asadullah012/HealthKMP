package com.viktormykhailiv.kmp.health.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.viktormykhailiv.kmp.health.HealthManager
import com.viktormykhailiv.kmp.health.sample.LocalHealthManager

@Composable
fun HealthNavHost(
    navController: NavHostController,
    healthManager: HealthManager,
) {
    CompositionLocalProvider(
        LocalHealthManager provides healthManager,
        LocalNavController provides navController,
    ) {
        NavHost(
            navController = navController,
            startDestination = NavDestinations.Root::class,
        ) {
            buildHealthNavGraph(navController, healthManager)
        }
    }
}