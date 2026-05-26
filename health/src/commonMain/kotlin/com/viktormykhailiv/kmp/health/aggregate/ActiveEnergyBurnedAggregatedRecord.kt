package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.ActiveEnergyBurned
import com.viktormykhailiv.kmp.health.units.Energy
import kotlin.time.Instant

/**
 * Captures the aggregated user's total energy burned.
 */
data class ActiveEnergyBurnedAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val energy: Energy,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = ActiveEnergyBurned
}
