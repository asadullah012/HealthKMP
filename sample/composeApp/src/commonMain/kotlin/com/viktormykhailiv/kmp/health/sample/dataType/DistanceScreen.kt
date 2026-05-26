package com.viktormykhailiv.kmp.health.sample.dataType

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.aggregate.DistanceAggregatedRecord
import com.viktormykhailiv.kmp.health.generateManualEntryMetadata
import com.viktormykhailiv.kmp.health.records.DistanceRecord
import com.viktormykhailiv.kmp.health.sample.dataType.base.DataTypeTextFieldScreen
import com.viktormykhailiv.kmp.health.units.Length
import com.viktormykhailiv.kmp.health.units.meters
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

@Composable
fun DistanceScreen() {
    DataTypeTextFieldScreen(
        title = "Distance, m",
        type = HealthDataType.Distance,
        initialValue = { Random.nextInt(1, 1000).toDouble() },
        serializer = { it.toString() },
        deserializer = { it.toDoubleOrNull() ?: 0.0 },
        writer = { distance ->
            listOf(
                DistanceRecord(
                    startTime = Clock.System.now().minus(1.hours),
                    endTime = Clock.System.now(),
                    distance = Length.meters(distance),
                    metadata = generateManualEntryMetadata(),
                ),
            )
        },
        aggregatedContent = { record: DistanceAggregatedRecord ->
            Text("Total ${record.distance}")
        },
        listContent = { distance ->
            val average = distance.map { it.distance.inMeters }.average().meters
            val total = distance.sumOf { it.distance.inMeters }.meters
            Text("Average $average")
            Text("Total $total")
        },
    )
}
