package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.units.kilocalories
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class ActiveEnergyBurnedRecordTest {

    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val endTime = Instant.fromEpochMilliseconds(2000)

    @Test
    fun validActiveEnergyBurnedRecord_shouldBeCreated() {
        ActiveEnergyBurnedRecord(
            startTime = startTime,
            endTime = endTime,
            energy = 100.kilocalories,
            metadata = Metadata.unknownRecordingMethod()
        )
    }

    @Test
    fun activeEnergyBurnedRecordWithZeroEnergy_shouldPass() {
        ActiveEnergyBurnedRecord(
            startTime = startTime,
            endTime = endTime,
            energy = 0.kilocalories,
            metadata = Metadata.unknownRecordingMethod()
        )
    }

    @Test
    fun activeEnergyBurnedRecordWithNegativeEnergy_shouldFail() {
        assertFailsWith<IllegalArgumentException> {
            ActiveEnergyBurnedRecord(
                startTime = startTime,
                endTime = endTime,
                energy = (-1).kilocalories,
                metadata = Metadata.unknownRecordingMethod()
            )
        }
    }

    @Test
    fun activeEnergyBurnedRecordWithInvalidTimeRange_shouldFail() {
        assertFailsWith<IllegalArgumentException> {
            ActiveEnergyBurnedRecord(
                startTime = endTime,
                endTime = startTime,
                energy = 100.kilocalories,
                metadata = Metadata.unknownRecordingMethod()
            )
        }
    }
}
