package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.ActiveEnergyBurned
import com.viktormykhailiv.kmp.health.IntervalRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Energy
import com.viktormykhailiv.kmp.health.units.kilocalories
import kotlin.time.Instant

/**
 * Total energy burned by the user (in kilocalories), including active & basal energy burned (BMR).
 * Each record represents the total kilocalories burned over a time interval.
 */
data class ActiveEnergyBurnedRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    val energy: Energy,
    override val metadata: Metadata = Metadata.unknownRecordingMethod(),
) : IntervalRecord {

    override val dataType: HealthDataType = ActiveEnergyBurned

    init {
        require(startTime < endTime) { "startTime must be before endTime." }
        energy.requireNotLess(other = energy.zero(), "energy")
        energy.requireNotMore(other = MAX_ENERGY, name = "energy")
    }

    private companion object {
        val MAX_ENERGY = 1000_000.kilocalories
    }
}
