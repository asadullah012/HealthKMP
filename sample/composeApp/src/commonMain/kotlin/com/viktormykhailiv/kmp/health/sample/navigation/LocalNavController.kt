package com.viktormykhailiv.kmp.health.sample.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavHostController not provided")
}
