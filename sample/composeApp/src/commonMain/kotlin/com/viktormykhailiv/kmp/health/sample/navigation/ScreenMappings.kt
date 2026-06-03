package com.viktormykhailiv.kmp.health.sample.navigation

import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.sample.dataType.BloodGlucoseScreen
import com.viktormykhailiv.kmp.health.sample.dataType.BloodPressureScreen
import com.viktormykhailiv.kmp.health.sample.dataType.BodyFatScreen
import com.viktormykhailiv.kmp.health.sample.dataType.BodyTemperatureScreen
import com.viktormykhailiv.kmp.health.sample.dataType.CyclingPedalingCadenceScreen
import com.viktormykhailiv.kmp.health.sample.dataType.ExerciseScreen
import com.viktormykhailiv.kmp.health.sample.dataType.HeartRateScreen
import com.viktormykhailiv.kmp.health.sample.dataType.HeightScreen
import com.viktormykhailiv.kmp.health.sample.dataType.LeanBodyMassScreen
import com.viktormykhailiv.kmp.health.sample.dataType.MenstruationFlowScreen
import com.viktormykhailiv.kmp.health.sample.dataType.MenstruationPeriodScreen
import com.viktormykhailiv.kmp.health.sample.dataType.OvulationTestScreen
import com.viktormykhailiv.kmp.health.sample.dataType.PowerScreen
import com.viktormykhailiv.kmp.health.sample.dataType.SexualActivityScreen
import com.viktormykhailiv.kmp.health.sample.dataType.SleepScreen
import com.viktormykhailiv.kmp.health.sample.dataType.StepsScreen
import com.viktormykhailiv.kmp.health.sample.dataType.WeightScreen

typealias ScreenContent = @Composable (onBackClick: () -> Unit) -> Unit

object ScreenMappings {
    val bodyMetrics: Map<NavDestinations, ScreenContent> = mapOf(
        NavDestinations.BodyFat to { onBackClick -> BodyFatScreen(onBackClick = onBackClick) },
        NavDestinations.Height to { onBackClick -> HeightScreen(onBackClick = onBackClick) },
        NavDestinations.LeanBodyMass to { onBackClick -> LeanBodyMassScreen(onBackClick = onBackClick) },
        NavDestinations.Weight to { onBackClick -> WeightScreen(onBackClick = onBackClick) },
    )

    val healthMetrics: Map<NavDestinations, ScreenContent> = mapOf(
        NavDestinations.BloodGlucose to { onBackClick -> BloodGlucoseScreen(onBackClick = onBackClick) },
        NavDestinations.BloodPressure to { onBackClick -> BloodPressureScreen(onBackClick = onBackClick) },
        NavDestinations.BodyTemperature to { onBackClick -> BodyTemperatureScreen(onBackClick = onBackClick) },
        NavDestinations.HeartRate to { onBackClick -> HeartRateScreen(onBackClick = onBackClick) },
    )

    val activity: Map<NavDestinations, ScreenContent> = mapOf(
        NavDestinations.CyclingPedalingCadence to { onBackClick -> CyclingPedalingCadenceScreen(onBackClick = onBackClick) },
        NavDestinations.Exercise to { onBackClick -> ExerciseScreen(onBackClick = onBackClick) },
        NavDestinations.Power to { onBackClick -> PowerScreen(onBackClick = onBackClick) },
        NavDestinations.Steps to { onBackClick -> StepsScreen(onBackClick = onBackClick) },
        NavDestinations.Sleep to { onBackClick -> SleepScreen(onBackClick = onBackClick) },
    )

    val reproductive: Map<NavDestinations, ScreenContent> = mapOf(
        NavDestinations.MenstruationFlow to { onBackClick -> MenstruationFlowScreen(onBackClick = onBackClick) },
        NavDestinations.MenstruationPeriod to { onBackClick -> MenstruationPeriodScreen(onBackClick = onBackClick) },
        NavDestinations.OvulationTest to { onBackClick -> OvulationTestScreen(onBackClick = onBackClick) },
        NavDestinations.SexualActivity to { onBackClick -> SexualActivityScreen(onBackClick = onBackClick) },
    )

    val all: Map<NavDestinations, ScreenContent> = bodyMetrics + healthMetrics + activity + reproductive

    fun getCategory(destination: NavDestinations): String? {
        return when {
            bodyMetrics.containsKey(destination) -> "Body Metrics"
            healthMetrics.containsKey(destination) -> "Health Metrics"
            activity.containsKey(destination) -> "Activity"
            reproductive.containsKey(destination) -> "Reproductive"
            else -> null
        }
    }
}