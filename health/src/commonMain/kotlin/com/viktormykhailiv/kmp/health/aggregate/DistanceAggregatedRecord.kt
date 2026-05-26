package com.viktormykhailiv.kmp.health.aggregate

import com.viktormykhailiv.kmp.health.HealthAggregatedRecord
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Distance
import com.viktormykhailiv.kmp.health.units.BloodGlucose
import com.viktormykhailiv.kmp.health.units.Length
import kotlin.time.Instant

/**
 * Captures the aggregated user's travelled distance.
 */
data class DistanceAggregatedRecord(
    val startTime: Instant,
    val endTime: Instant,
    val distance: Length,
) : HealthAggregatedRecord {

    override val dataType: HealthDataType = Distance
}
