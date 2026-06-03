package com.viktormykhailiv.kmp.health.sample.ui

import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord

data class ActiveBurnData(
    val burned: Int,
    val dailyTarget: Int
) {
    val remaining: Int get() = (dailyTarget - burned).coerceAtLeast(0)
    val percentage: Float get() = (burned.toFloat() / dailyTarget.toFloat()).coerceIn(0f, 1f)
}

data class HealthState(
    val heartRate: List<HeartRateRecord> = emptyList(),
    val steps: List<StepsRecord> = emptyList(),
    val bloodPressure: List<BloodPressureRecord> = emptyList(),
    val sleepData: List<SleepSessionRecord> = emptyList(),
    val exerciseHistory: List<ExerciseSessionRecord> = emptyList(),
)
