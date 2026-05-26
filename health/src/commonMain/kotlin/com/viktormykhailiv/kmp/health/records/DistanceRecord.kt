package com.viktormykhailiv.kmp.health.records

import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.HealthDataType.Distance
import com.viktormykhailiv.kmp.health.IntervalRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.requireNotLess
import com.viktormykhailiv.kmp.health.requireNotMore
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.meters
import kotlin.time.Instant

/**
 * Captures distance travelled by the user since the last reading. The total distance over an
 * interval can be calculated by adding together all the values during the interval. The start time
 * of each record should represent the start of the interval in which the distance was covered.
 *
 * If break downs are preferred in scenario of a long workout, consider writing multiple distance
 * records. The start time of each record should be equal to or greater than the end time of the
 * previous record.
 */
data class DistanceRecord(
    override val startTime: Instant,
    override val endTime: Instant,
    val distance: Length,
    override val metadata: Metadata = Metadata.unknownRecordingMethod(),
) : IntervalRecord {

    override val dataType: HealthDataType = Distance

    init {
        distance.requireNotLess(other = distance.zero(), name = "distance")
        distance.requireNotMore(other = MAX_DISTANCE, name = "distance")
        require(startTime < endTime) { "startTime must be before endTime." }
    }

    private companion object {
        val MAX_DISTANCE = 1000_000.meters
    }
}
