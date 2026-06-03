package com.viktormykhailiv.kmp.health.sample.domain

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import kotlin.time.Instant

interface HealthRepository {
    suspend fun isAvailable(): Result<Boolean>
    suspend fun isAuthorized(readTypes: List<HealthDataType>, writeTypes: List<HealthDataType>): Result<Boolean>
    suspend fun requestAuthorization(readTypes: List<HealthDataType>, writeTypes: List<HealthDataType>): Result<Boolean>
    suspend fun requestAuthorizationWithBackground(readTypes: List<HealthDataType>, writeTypes: List<HealthDataType>): Result<Boolean>
    suspend fun isRevokeAuthorizationSupported(): Result<Boolean>

    suspend fun isBackgroundSyncSupported(): Result<Boolean>
    suspend fun revokeAuthorization(): Result<Unit>
    suspend fun hasReadHealthDataInBackgroundPermission(): Result<Boolean>
    suspend fun requestReadHealthDataInBackgroundPermission(): Result<Boolean>
    suspend fun getRegionalPreferences(): Result<RegionalPreferences>
    suspend fun getAvailableHealthDataType(): List<HealthDataType>
    suspend fun readHeartRate(startTime: Instant, endTime: Instant) : Result<List<HeartRateRecord>>
    suspend fun readSteps(startTime: Instant, endTime: Instant) : Result<List<StepsRecord>>
    suspend fun readSleep(startTime: Instant, endTime: Instant) : Result<List<SleepSessionRecord>>
    suspend fun readExercise(startTime: Instant, endTime: Instant) : Result <List<ExerciseSessionRecord>>
    suspend fun readBloodPressure(startTime: Instant, endTime: Instant) : Result<List<BloodPressureRecord>>
}