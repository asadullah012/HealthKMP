package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.units.meters
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class DistanceRecordTest {

    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val endTime = Instant.fromEpochMilliseconds(2000)

    @Test
    fun validDistanceRecord_shouldBeCreated() {
        DistanceRecord(
            startTime = startTime,
            endTime = endTime,
            distance = 100.meters,
            metadata = Metadata.unknownRecordingMethod()
        )
    }

    @Test
    fun distanceRecordWithZeroDistance_shouldPass() {
        DistanceRecord(
            startTime = startTime,
            endTime = endTime,
            distance = 0.meters,
            metadata = Metadata.unknownRecordingMethod()
        )
    }

    @Test
    fun distanceRecordWithNegativeDistance_shouldFail() {
        assertFailsWith<IllegalArgumentException> {
            DistanceRecord(
                startTime = startTime,
                endTime = endTime,
                distance = (-1).meters,
                metadata = Metadata.unknownRecordingMethod()
            )
        }
    }

    @Test
    fun distanceRecordWithInvalidTimeRange_shouldFail() {
        assertFailsWith<IllegalArgumentException> {
            DistanceRecord(
                startTime = endTime,
                endTime = startTime,
                distance = 100.meters,
                metadata = Metadata.unknownRecordingMethod()
            )
        }
    }
}
