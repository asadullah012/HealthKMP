package com.viktormykhailiv.kmp.health.sample

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.viktormykhailiv.kmp.health.HealthManager
import com.viktormykhailiv.kmp.health.sample.di.appModule
import com.viktormykhailiv.kmp.health.sample.navigation.HealthNavHost
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun SampleApp() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        KoinContext {
            AppContent()
        }
    }
}

@Composable
private fun AppContent() {
    val healthManager: HealthManager = koinInject()
    val navController = rememberNavController()
    HealthNavHost(navController, healthManager)
}
