package com.viktormykhailiv.kmp.health

import com.viktormykhailiv.kmp.health.records.ActiveEnergyBurnedRecord
import com.viktormykhailiv.kmp.health.records.BloodGlucoseRecord
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.BodyFatRecord
import com.viktormykhailiv.kmp.health.records.BodyTemperatureRecord
import com.viktormykhailiv.kmp.health.records.CyclingPedalingCadenceRecord
import com.viktormykhailiv.kmp.health.records.DistanceRecord
import com.viktormykhailiv.kmp.health.records.ExerciseLap
import com.viktormykhailiv.kmp.health.records.ExerciseSegment
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.ExerciseType
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.HeightRecord
import com.viktormykhailiv.kmp.health.records.LeanBodyMassRecord
import com.viktormykhailiv.kmp.health.records.MenstruationFlowRecord
import com.viktormykhailiv.kmp.health.records.MenstruationPeriodRecord
import com.viktormykhailiv.kmp.health.records.OvulationTestRecord
import com.viktormykhailiv.kmp.health.records.PowerRecord
import com.viktormykhailiv.kmp.health.records.SexualActivityRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.records.WeightRecord
import com.viktormykhailiv.kmp.health.records.metadata.Metadata
import com.viktormykhailiv.kmp.health.region.TemperatureRegionalPreference
import com.viktormykhailiv.kmp.health.units.BloodGlucose
import com.viktormykhailiv.kmp.health.units.celsius
import com.viktormykhailiv.kmp.health.units.kilocalories
import com.viktormykhailiv.kmp.health.units.kilograms
import com.viktormykhailiv.kmp.health.units.meters
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import com.viktormykhailiv.kmp.health.units.percent
import com.viktormykhailiv.kmp.health.units.watts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import androidx.health.connect.client.records.BloodGlucoseRecord as HCBloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord as HCBloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord as HCBodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord as HCBodyTemperatureRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord as HCPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord as HCDistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord as HCExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord as HCHeartRateRecord
import androidx.health.connect.client.records.HeightRecord as HCHeightRecord
import androidx.health.connect.client.records.LeanBodyMassRecord as HCLeanBodyMassRecord
import androidx.health.connect.client.records.MenstruationFlowRecord as HCMenstruationFlowRecord
import androidx.health.connect.client.records.MenstruationPeriodRecord as HCMenstruationPeriodRecord
import androidx.health.connect.client.records.OvulationTestRecord as HCOvulationTestRecord
import androidx.health.connect.client.records.PowerRecord as HCPowerRecord
import androidx.health.connect.client.records.SexualActivityRecord as HCSexualActivityRecord
import androidx.health.connect.client.records.SleepSessionRecord as HCSleepSessionRecord
import androidx.health.connect.client.records.StepsRecord as HCStepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord as HCTotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord as HCWeightRecord

class MappingTest {

    private val time = Instant.fromEpochMilliseconds(1000)
    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val endTime = Instant.fromEpochMilliseconds(2000)
    private val metadata = Metadata.unknownRecordingMethod()

    private val tempPreference = { TemperatureRegionalPreference.Celsius }

    @Test
    fun activeEnergyBurnedMapping() {
        val common = ActiveEnergyBurnedRecord(
            startTime = startTime,
            endTime = endTime,
            energy = 100.kilocalories,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCTotalCaloriesBurnedRecord
        assertEquals(100.0, hc.energy.inKilocalories)

        val mappedBack = hc.toHealthRecord(tempPreference) as ActiveEnergyBurnedRecord
        assertEquals(common.energy, mappedBack.energy)
    }

    @Test
    fun bloodGlucoseMapping() {
        val common = BloodGlucoseRecord(
            time = time,
            level = BloodGlucose.millimolesPerLiter(5.5),
            specimenSource = BloodGlucoseRecord.SpecimenSource.CapillaryBlood,
            mealType = null,
            relationToMeal = BloodGlucoseRecord.RelationToMeal.BeforeMeal,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCBloodGlucoseRecord
        assertEquals(5.5, hc.level.inMillimolesPerLiter)

        val mappedBack = hc.toHealthRecord(tempPreference) as BloodGlucoseRecord
        assertEquals(common.level, mappedBack.level)
        assertEquals(common.relationToMeal, mappedBack.relationToMeal)
    }

    @Test
    fun bloodPressureMapping() {
        val common = BloodPressureRecord(
            time = time,
            systolic = 120.millimetersOfMercury,
            diastolic = 80.millimetersOfMercury,
            bodyPosition = BloodPressureRecord.BodyPosition.SittingDown,
            measurementLocation = BloodPressureRecord.MeasurementLocation.LeftUpperArm,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCBloodPressureRecord
        assertEquals(120.0, hc.systolic.inMillimetersOfMercury)
        assertEquals(80.0, hc.diastolic.inMillimetersOfMercury)

        val mappedBack = hc.toHealthRecord(tempPreference) as BloodPressureRecord
        assertEquals(common.systolic, mappedBack.systolic)
        assertEquals(common.diastolic, mappedBack.diastolic)
    }

    @Test
    fun bodyFatMapping() {
        val common = BodyFatRecord(time = time, percentage = 20.percent, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCBodyFatRecord
        assertEquals(20.0, hc.percentage.value)

        val mappedBack = hc.toHealthRecord(tempPreference) as BodyFatRecord
        assertEquals(common.percentage, mappedBack.percentage)
    }

    @Test
    fun bodyTemperatureMapping() {
        val common = BodyTemperatureRecord(
            time = time,
            temperature = 36.6.celsius,
            measurementLocation = BodyTemperatureRecord.MeasurementLocation.Mouth,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCBodyTemperatureRecord
        assertEquals(36.6, hc.temperature.inCelsius)

        val mappedBack = hc.toHealthRecord(tempPreference) as BodyTemperatureRecord
        assertEquals(common.temperature, mappedBack.temperature)
        assertEquals(common.measurementLocation, mappedBack.measurementLocation)
    }

    @Test
    fun cyclingPedalingCadenceMapping() {
        val common = CyclingPedalingCadenceRecord(
            startTime = startTime,
            endTime = endTime,
            samples = listOf(
                CyclingPedalingCadenceRecord.Sample(time = startTime, revolutionsPerMinute = 90.0)
            ),
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCPedalingCadenceRecord
        assertEquals(90.0, hc.samples.first().revolutionsPerMinute)

        val mappedBack = hc.toHealthRecord(tempPreference) as CyclingPedalingCadenceRecord
        assertEquals(common.samples, mappedBack.samples)
    }

    @Test
    fun distanceMapping() {
        val common = DistanceRecord(
            startTime = startTime,
            endTime = endTime,
            distance = 100.meters,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCDistanceRecord
        assertEquals(100.0, hc.distance.inMeters)

        val mappedBack = hc.toHealthRecord(tempPreference) as DistanceRecord
        assertEquals(common.distance, mappedBack.distance)
    }

    @Test
    fun exerciseSessionMapping() {
        val common = ExerciseSessionRecord(
            startTime = startTime,
            endTime = endTime,
            exerciseType = ExerciseType.Running,
            metadata = metadata,
            segments = listOf(
                ExerciseSegment(
                    startTime = startTime,
                    endTime = endTime,
                    segmentType = ExerciseSegment.Type.Running
                )
            ),
            laps = listOf(
                ExerciseLap(
                    startTime = startTime,
                    endTime = endTime,
                    length = 100.meters
                )
            ),
            totalDistance = null,
            totalEnergyBurned = null,
            exerciseRoute = null
        )
        val hc = common.toHCRecord(tempPreference) as HCExerciseSessionRecord
        assertEquals(startTime.toJavaInstant(), hc.startTime)

        val mappedBack = hc.toHealthRecord(tempPreference) as ExerciseSessionRecord
        assertEquals(common.exerciseType, mappedBack.exerciseType)
        assertEquals(common.segments, mappedBack.segments)
        assertEquals(common.laps, mappedBack.laps)
        assertEquals(common.totalDistance, mappedBack.totalDistance)
        assertEquals(common.totalEnergyBurned, mappedBack.totalEnergyBurned)
    }

    @Test
    fun heartRateMapping() {
        val common = HeartRateRecord(
            startTime = startTime,
            endTime = endTime,
            samples = listOf(HeartRateRecord.Sample(time = startTime, beatsPerMinute = 70)),
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCHeartRateRecord
        assertEquals(70, hc.samples.first().beatsPerMinute)

        val mappedBack = hc.toHealthRecord(tempPreference) as HeartRateRecord
        assertEquals(common.samples, mappedBack.samples)
    }

    @Test
    fun heightMapping() {
        val common = HeightRecord(time = time, height = 1.8.meters, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCHeightRecord
        assertEquals(1.8, hc.height.inMeters)

        val mappedBack = hc.toHealthRecord(tempPreference) as HeightRecord
        assertEquals(common.height, mappedBack.height)
    }

    @Test
    fun leanBodyMassMapping() {
        val common = LeanBodyMassRecord(time = time, mass = 70.kilograms, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCLeanBodyMassRecord
        assertEquals(70.0, hc.mass.inKilograms)

        val mappedBack = hc.toHealthRecord(tempPreference) as LeanBodyMassRecord
        assertEquals(common.mass, mappedBack.mass)
    }

    @Test
    fun menstruationFlowMapping() {
        val common = MenstruationFlowRecord(
            time = time,
            flow = MenstruationFlowRecord.Flow.Medium,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCMenstruationFlowRecord
        assertEquals(
            HCMenstruationFlowRecord.FLOW_MEDIUM,
            hc.flow
        )

        val mappedBack = hc.toHealthRecord(tempPreference) as MenstruationFlowRecord
        assertEquals(common.flow, mappedBack.flow)
    }

    @Test
    fun menstruationPeriodMapping() {
        val common = MenstruationPeriodRecord(
            startTime = startTime,
            endTime = endTime,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCMenstruationPeriodRecord
        assertEquals(startTime.toJavaInstant(), hc.startTime)

        val mappedBack = hc.toHealthRecord(tempPreference) as MenstruationPeriodRecord
        assertEquals(common.startTime, mappedBack.startTime)
        assertEquals(common.endTime, mappedBack.endTime)
    }

    @Test
    fun ovulationTestMapping() {
        val common = OvulationTestRecord(
            time = time,
            result = OvulationTestRecord.Result.Positive,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCOvulationTestRecord
        assertEquals(HCOvulationTestRecord.RESULT_POSITIVE, hc.result)

        val mappedBack = hc.toHealthRecord(tempPreference) as OvulationTestRecord
        assertEquals(common.result, mappedBack.result)
    }

    @Test
    fun powerMapping() {
        val common = PowerRecord(
            startTime = startTime,
            endTime = endTime,
            samples = listOf(PowerRecord.Sample(time = startTime, power = 200.watts)),
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCPowerRecord
        assertEquals(200.0, hc.samples.first().power.inWatts)

        val mappedBack = hc.toHealthRecord(tempPreference) as PowerRecord
        assertEquals(common.samples, mappedBack.samples)
    }

    @Test
    fun sexualActivityMapping() {
        val common = SexualActivityRecord(
            time = time,
            protection = SexualActivityRecord.Protection.Protected,
            metadata = metadata
        )
        val hc = common.toHCRecord(tempPreference) as HCSexualActivityRecord
        assertEquals(HCSexualActivityRecord.PROTECTION_USED_PROTECTED, hc.protectionUsed)

        val mappedBack = hc.toHealthRecord(tempPreference) as SexualActivityRecord
        assertEquals(common.protection, mappedBack.protection)
    }

    @Test
    fun sleepSessionMapping() {
        val common = SleepSessionRecord(
            startTime = startTime,
            endTime = endTime,
            metadata = metadata,
            stages = listOf(
                SleepSessionRecord.Stage(
                    startTime = startTime,
                    endTime = endTime,
                    type = SleepStageType.Deep
                )
            )
        )
        val hc = common.toHCRecord(tempPreference) as HCSleepSessionRecord
        assertEquals(startTime.toJavaInstant(), hc.startTime)

        val mappedBack = hc.toHealthRecord(tempPreference) as SleepSessionRecord
        assertEquals(common.startTime, mappedBack.startTime)
        assertEquals(common.endTime, mappedBack.endTime)
        assertEquals(common.stages, mappedBack.stages)
    }

    @Test
    fun stepsMapping() {
        val common = StepsRecord(startTime = startTime, endTime = endTime, count = 1000, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCStepsRecord
        assertEquals(1000, hc.count)

        val mappedBack = hc.toHealthRecord(tempPreference) as StepsRecord
        assertEquals(common.count, mappedBack.count)
    }

    @Test
    fun weightMapping() {
        val common = WeightRecord(time = time, weight = 80.kilograms, metadata = metadata)
        val hc = common.toHCRecord(tempPreference) as HCWeightRecord
        assertEquals(80.0, hc.weight.inKilograms)

        val mappedBack = hc.toHealthRecord(tempPreference) as WeightRecord
        assertEquals(common.weight, mappedBack.weight)
    }
}
