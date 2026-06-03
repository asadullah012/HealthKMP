package com.viktormykhailiv.kmp.health.sample.data

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthManager
import com.viktormykhailiv.kmp.health.readBloodPressure
import com.viktormykhailiv.kmp.health.readExercise
import com.viktormykhailiv.kmp.health.readHeartRate
import com.viktormykhailiv.kmp.health.readSleep
import com.viktormykhailiv.kmp.health.readSteps
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import com.viktormykhailiv.kmp.health.sample.domain.HealthRepository
import kotlin.time.Instant

class HealthRepositoryImpl(
    private val healthManager: HealthManager,
) : HealthRepository {
    override suspend fun isAvailable(): Result<Boolean> = healthManager.isAvailable()

    override suspend fun isAuthorized(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> = healthManager.isAuthorized(readTypes, writeTypes)

    override suspend fun requestAuthorization(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> = healthManager.requestAuthorization(readTypes, writeTypes)

    override suspend fun requestAuthorizationWithBackground(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ): Result<Boolean> = healthManager.requestAuthorization(readTypes, writeTypes, true)

    override suspend fun isRevokeAuthorizationSupported(): Result<Boolean> =
        healthManager.isRevokeAuthorizationSupported()

    override suspend fun isBackgroundSyncSupported(): Result<Boolean> =
        healthManager.isBackgroundSyncSupported()

    override suspend fun revokeAuthorization() = healthManager.revokeAuthorization()

    override suspend fun hasReadHealthDataInBackgroundPermission(): Result<Boolean> =
        healthManager.hasReadHealthDataInBackgroundPermission()

    override suspend fun requestReadHealthDataInBackgroundPermission(): Result<Boolean> =
        healthManager.requestReadHealthDataInBackgroundPermission()

    override suspend fun getRegionalPreferences(): Result<RegionalPreferences> =
        healthManager.getRegionalPreferences()

    override suspend fun getAvailableHealthDataType(): List<HealthDataType> = listOf(
        HealthDataType.BloodGlucose,
        HealthDataType.BloodPressure,
        HealthDataType.BodyFat,
        HealthDataType.BodyTemperature,
        HealthDataType.CyclingPedalingCadence,
        HealthDataType.Exercise(),
        HealthDataType.HeartRate,
        HealthDataType.Height,
        HealthDataType.LeanBodyMass,
        HealthDataType.MenstruationFlow,
        HealthDataType.MenstruationPeriod,
        HealthDataType.OvulationTest,
        HealthDataType.Power,
        HealthDataType.SexualActivity,
        HealthDataType.Sleep,
        HealthDataType.Steps,
        HealthDataType.Weight,
    )

    override suspend fun readHeartRate(
        startTime: Instant,
        endTime: Instant
    ): Result<List<HeartRateRecord>> {
        return healthManager.readHeartRate(startTime, endTime)
    }

    override suspend fun readSteps(
        startTime: Instant,
        endTime: Instant
    ): Result<List<StepsRecord>> {
        return healthManager.readSteps(startTime, endTime)
    }

    override suspend fun readSleep(
        startTime: Instant,
        endTime: Instant
    ): Result<List<SleepSessionRecord>> {
        return healthManager.readSleep(startTime, endTime)
    }

    override suspend fun readExercise(
        startTime: Instant,
        endTime: Instant
    ): Result<List<ExerciseSessionRecord>> {
        return healthManager.readExercise(startTime,endTime)
    }

    override suspend fun readBloodPressure(
        startTime: Instant,
        endTime: Instant
    ): Result<List<BloodPressureRecord>> {
        return healthManager.readBloodPressure(startTime, endTime)
    }
}