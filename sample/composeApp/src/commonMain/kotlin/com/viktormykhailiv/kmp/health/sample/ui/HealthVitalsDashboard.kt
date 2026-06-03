package com.viktormykhailiv.kmp.health.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.viktormykhailiv.kmp.health.duration
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.ExerciseType
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.metadata.Device
import com.viktormykhailiv.kmp.health.records.metadata.DeviceType
import com.viktormykhailiv.kmp.health.sample.presentation.HomeScreenViewModel
import com.viktormykhailiv.kmp.health.sample.presentation.HomeUiState

@Composable
fun HealthVitalsDashboard(viewModel: HomeScreenViewModel) {
    val state by viewModel.uiState.collectAsState()
    val healthData = (state as? HomeUiState.Ready)?.healthData

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.syncData()
    }
    if(healthData != null){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeartRateCard(healthData.heartRate)
            StepsCard(healthData.steps)
            ExerciseHistoryCard(healthData.exerciseHistory)
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Heart Rate ────────────────────────────────────────────────────────────────
@Composable
private fun HeartRateCard(data: List<HeartRateRecord>) {
    if (data.isEmpty()) {
        VitalsCard {
            SectionLabel("HEART RATE")
            Spacer(Modifier.height(4.dp))
            Text("No Data")
        }
        return
    }

    val grouped = data.groupBy { it.metadata.device }

    grouped.forEach { (device, records) ->
        val readings = records.takeLast(10).map { record ->
            record.samples
                .takeIf { it.isNotEmpty() }
                ?.map { it.beatsPerMinute }
                ?.average()
                ?.toInt()
                ?: 0
        }
        println("HeartRate $device $readings")

        val averageReading = readings.average().toInt()
        val deviceName = buildString {
            device?.manufacturer?.let { append(it) }
            device?.model?.let {
                if (isNotEmpty()) append(" ")
                append(it)
            }
        }.takeIf { it.isNotBlank() } ?: device?.type?.displayName() ?: "Unknown Device"

        VitalsCard {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                Column {
                    SectionLabel("HEART RATE")
                    Spacer(Modifier.height(2.dp))
                    Text(
                        deviceName,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "$averageReading",
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp,
                            color = TextPrimary,
                            lineHeight = 42.sp,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "BPM",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }
                IconBadge(HeartRedLight) {
                    Icon(
                        Icons.Filled.Favorite,
                        null,
                        tint = HeartRed,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            HeartRateBarChart(readings)
        }
    }
}

// ── Steps ─────────────────────────────────────────────────────────────────────
@Composable
private fun StepsCard(data: List<StepsRecord>) {
    val target = 10000
    val grouped = data.groupBy { it.metadata.device }

    if (grouped.isEmpty()) {
        VitalsCard (Modifier.fillMaxWidth()){
            SectionLabel("STEPS")
            Spacer(Modifier.height(12.dp))
            Text("No Data")
        }
        return
    }

    grouped.forEach { (device, records) ->
        val stepCount = records.sumOf { it.count }
        val percentage = (stepCount / target.toFloat()).coerceAtMost(1f)
        val deviceName = buildDeviceName(device)

        VitalsCard (Modifier.fillMaxWidth()){
            SectionLabel("STEPS")
            Spacer(Modifier.height(6.dp))
            Text(
                deviceName,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(12.dp))
            StepsCircle(
                current = stepCount,
                goal = target,
                percentage = percentage,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.DirectionsWalk,
                    null,
                    tint = TealPrimary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "${(percentage * 1000).toInt() / 10.0}% reached",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TealPrimary,
                )
            }
        }
    }
}

// ── Blood Pressure ───────────────────────────────────────────────────────────────
@Composable
private fun BloodPressureCard(data: List<BloodPressureRecord>, modifier: Modifier) {
    if (data.isEmpty()) {
        VitalsCard(modifier) {
            SectionLabel("BLOOD PRESSURE")
            Spacer(Modifier.height(12.dp))
            Text("No Data")
        }
        return
    }

    val grouped = data.groupBy { it.metadata.device }

    grouped.forEach { (device, records) ->
        val lastRecord = records.maxByOrNull { it.time } ?: return@forEach
        val deviceName = buildDeviceName(device)

        VitalsCard(modifier) {
            SectionLabel("BLOOD PRESSURE")
            Spacer(Modifier.height(6.dp))
            Text(
                deviceName,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(OrangeLight) {
                    Icon(
                        Icons.Filled.LocalFireDepartment,
                        null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "${lastRecord.systolic}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = TextPrimary,
                        lineHeight = 28.sp,
                    )
                    Text(
                        "${lastRecord.diastolic}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary,
                        lineHeight = 28.sp,
                    )
                }
            }
            lastRecord.bodyPosition.toString().let { position ->
                Spacer(Modifier.height(8.dp))
                Text(
                    position,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 14.sp,
                )
            }
            lastRecord.measurementLocation.toString().let { location ->
                Spacer(Modifier.height(4.dp))
                Text(
                    location,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}

// ── Sleep ─────────────────────────────────────────────────────────────────────
@Composable
private fun SleepCard(data: List<SleepSessionRecord>) {
    if (data.isEmpty()) {
        VitalsCard {
            SectionLabel("SLEEP ANALYSIS")
            Spacer(Modifier.height(6.dp))
            Text("No Data")
        }
        return
    }

    val grouped = data.groupBy { it.metadata.device }

    grouped.forEach { (device, records) ->
        val totalMins = records.sumOf { it.duration.inWholeMinutes }
        val deviceName = buildDeviceName(device)

        VitalsCard {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                Column {
                    SectionLabel("SLEEP ANALYSIS")
                    Spacer(Modifier.height(2.dp))
                    Text(
                        deviceName,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(6.dp))
                    if (totalMins == 0L) {
                        Text("No Sleep Data")
                    } else {
                        Text(
                            "${totalMins / 60}h ${totalMins % 60}m",
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = TextPrimary,
                        )
                    }
                }
                IconBadge(PurpleLight) {
                    Icon(
                        Icons.Outlined.NightlightRound,
                        null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            records.forEach { session ->
                val stages = session.stages.ifEmpty {
                    listOf(
                        SleepSessionRecord.Stage(
                            startTime = session.startTime,
                            endTime = session.endTime,
                            type = SleepStageType.Unknown,
                        )
                    )
                }
                stages.forEach { stage ->
                    val stageMins = (stage.endTime - stage.startTime).inWholeMinutes
                    val h = stageMins / 60
                    val m = stageMins % 60
                    Spacer(Modifier.height(12.dp))
                    SleepPhaseRow(
                        label = stage.type.toString(),
                        h = h.toInt(),
                        m = m.toInt(),
                        totalMins = totalMins.toInt(),
                        color = TealPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepPhaseRow(label: String, h: Int, m: Int, totalMins: Int, color: Color) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text("${h}h ${m}m", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
    }
    Spacer(Modifier.height(6.dp))
    AnimatedProgressBar(
        progress = (h * 60 + m).toFloat() / totalMins,
        trackColor = color.copy(alpha = 0.2f),
        fillColor = color,
        height = 6.dp
    )
}

// ── Exercise History ──────────────────────────────────────────────────────────
@Composable
private fun ExerciseHistoryCard(entries: List<ExerciseSessionRecord>) {
    if (entries.isEmpty()) {
        VitalsCard {
            SectionLabel("EXERCISE HISTORY")
            Spacer(Modifier.height(12.dp))
            Text("No Data")
        }
        return
    }

    val grouped = entries.groupBy { it.metadata.device }

    grouped.forEach { (device, records) ->
        val deviceName = buildDeviceName(device)

        VitalsCard {
            SectionLabel("EXERCISE HISTORY")
            Spacer(Modifier.height(6.dp))
            Text(
                deviceName,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(12.dp))
            records.forEachIndexed { i, entry ->
                ExerciseRow(entry)
                if (i < records.lastIndex) HorizontalDivider(
                    Modifier.padding(vertical = 10.dp),
                    color = DividerColor,
                    thickness = 0.5.dp,
                )
            }
        }
    }
}
@Composable
private fun ExerciseRow(entry: ExerciseSessionRecord) {
    data class Style(val bg: Color, val tint: Color, val icon: ImageVector)
    val style = when (entry.exerciseType) {
        ExerciseType.Running  -> Style(TealLight,             TealPrimary,            Icons.Filled.DirectionsRun)
        ExerciseType.Biking  -> Style(Color(0xFFE3F2FD),    Color(0xFF1565C0),      Icons.Filled.DirectionsBike)
        ExerciseType.SwimmingPool, ExerciseType.SwimmingOpenWater -> Style(PurpleLight,           PurpleAccent,           Icons.Filled.Pool)
        ExerciseType.Gymnastics      -> Style(OrangeLight,           OrangeAccent,           Icons.Filled.FitnessCenter)
        ExerciseType.Yoga     -> Style(Color(0xFFFCE4EC),    Color(0xFFE91E63),      Icons.Filled.SelfImprovement)
        ExerciseType.Walking  -> Style(TealSurface,           TealPrimary,            Icons.Filled.DirectionsWalk)
        else -> Style(Color(0xFFFCE4EC),    Color(0xFFE91E63),      Icons.Filled.SelfImprovement)
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconBadge(style.bg) {
            Icon(
                style.icon,
                null,
                tint = style.tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("${entry.exerciseType}", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
            Text("${entry.duration.inWholeMinutes} min", fontSize = 12.sp, color = TextSecondary)
            Text("${entry.totalEnergyBurned?.inKilocalories?.roundTo(2)} KCAL", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
        }
    }
}


fun DeviceType.displayName(): String? = when (this) {
    DeviceType.Watch -> "Watch"
    DeviceType.Phone -> "Phone"
    DeviceType.Scale -> "Scale"
    DeviceType.Ring -> "Ring"
    DeviceType.HeadMounted -> "Head Mounted"
    DeviceType.FitnessBand -> "Fitness Band"
    DeviceType.ChestStrap -> "Chest Strap"
    DeviceType.SmartDisplay -> "Smart Display"
    DeviceType.Unknown -> null
}

fun buildDeviceName(device: Device?): String = buildString {
    device?.manufacturer?.let { append(it) }
    device?.model?.let {
        if (isNotEmpty()) append(" ")
        append(it)
    }
}.takeIf { it.isNotBlank() } ?: device?.type?.displayName() ?: "Unknown Device"
