@file:OptIn(UnsafeNumber::class)
@file:Suppress("UNCHECKED_CAST")

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
import com.viktormykhailiv.kmp.health.units.Mass
import com.viktormykhailiv.kmp.health.units.Temperature
import com.viktormykhailiv.kmp.health.units.celsius
import com.viktormykhailiv.kmp.health.units.kilocalories
import com.viktormykhailiv.kmp.health.units.kilograms
import com.viktormykhailiv.kmp.health.units.meters
import com.viktormykhailiv.kmp.health.units.millimetersOfMercury
import com.viktormykhailiv.kmp.health.units.percent
import com.viktormykhailiv.kmp.health.units.watts
import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKCategoryValueMenstrualFlowMedium
import platform.HealthKit.HKCategoryValueOvulationTestResultLuteinizingHormoneSurge
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepDeep
import platform.HealthKit.HKCategoryValueMenstrualFlowUnspecified
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKUnit
import platform.HealthKit.HKWorkout
import platform.HealthKit.HKWorkoutActivityTypeRunning
import platform.HealthKit.HKUnitMolarMassBloodGlucose
import platform.HealthKit.countUnit
import platform.HealthKit.degreeFahrenheitUnit
import platform.HealthKit.kilocalorieUnit
import platform.HealthKit.meterUnit
import platform.HealthKit.millimeterOfMercuryUnit
import platform.HealthKit.percentUnit
import platform.HealthKit.poundUnit
import platform.HealthKit.wattUnit
import platform.HealthKit.literUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.moleUnitWithMolarMass
import platform.HealthKit.unitDividedByUnit

class MappingTest {

    private val time = Instant.fromEpochMilliseconds(1000)
    private val startTime = Instant.fromEpochMilliseconds(1000)
    private val endTime = Instant.fromEpochMilliseconds(2000)
    private val metadata = Metadata.unknownRecordingMethod()

    private val tempPreference = suspend { TemperatureRegionalPreference.Celsius }

    @Test
    fun activeEnergyBurnedMapping() = runTest {
        val common = ActiveEnergyBurnedRecord(
            startTime = startTime,
            endTime = endTime,
            energy = 100.kilocalories,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(100.0, hk.quantity.doubleValueForUnit(HKUnit.kilocalorieUnit()))

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as ActiveEnergyBurnedRecord
        assertEquals(common.energy, mappedBack.energy)
    }

    @Test
    fun bloodGlucoseMapping() = runTest {
        val common = BloodGlucoseRecord(
            time = time,
            level = BloodGlucose.millimolesPerLiter(5.5),
            specimenSource = null,
            mealType = null,
            relationToMeal = BloodGlucoseRecord.RelationToMeal.BeforeMeal,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        val bloodGlucoseUnit = HKUnit.moleUnitWithMolarMass(HKUnitMolarMassBloodGlucose).unitDividedByUnit(HKUnit.literUnit())
        assertEquals(5.5, hk.quantity.doubleValueForUnit(bloodGlucoseUnit) * 1000, 0.1)

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as BloodGlucoseRecord
        assertEquals(common.level.inMillimolesPerLiter, mappedBack.level.inMillimolesPerLiter, 0.1)
    }

    @Test
    fun bloodPressureMapping() = runTest {
        val common = BloodPressureRecord(
            time = time,
            systolic = 120.millimetersOfMercury,
            diastolic = 80.millimetersOfMercury,
            bodyPosition = null,
            measurementLocation = null,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val systolicHk = hkList.find { it is HKQuantitySample && it.quantityType.identifier == HKQuantityTypeIdentifierBloodPressureSystolic } as HKQuantitySample
        val diastolicHk = hkList.find { it is HKQuantitySample && it.quantityType.identifier == HKQuantityTypeIdentifierBloodPressureDiastolic } as HKQuantitySample
        
        assertEquals(120.0, systolicHk.quantity.doubleValueForUnit(HKUnit.millimeterOfMercuryUnit()))
        assertEquals(80.0, diastolicHk.quantity.doubleValueForUnit(HKUnit.millimeterOfMercuryUnit()))

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as BloodPressureRecord
        assertEquals(common.systolic, mappedBack.systolic)
        assertEquals(common.diastolic, mappedBack.diastolic)
    }

    @Test
    fun bodyFatMapping() = runTest {
        val common = BodyFatRecord(time = time, percentage = 20.percent, metadata = metadata)
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(20.0, hk.quantity.doubleValueForUnit(HKUnit.percentUnit()) * 100.0, 0.1)

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as BodyFatRecord
        assertEquals(common.percentage, mappedBack.percentage)
    }

    @Test
    fun bodyTemperatureMapping() = runTest {
        val common = BodyTemperatureRecord(
            time = time,
            temperature = 36.6.celsius,
            measurementLocation = null,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(36.6, Temperature.fahrenheit(hk.quantity.doubleValueForUnit(HKUnit.degreeFahrenheitUnit())).inCelsius, 0.1)

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as BodyTemperatureRecord
        assertEquals(common.temperature.inCelsius, mappedBack.temperature.inCelsius, 0.1)
    }

    @Test
    fun cyclingPedalingCadenceMapping() = runTest {
        val common = CyclingPedalingCadenceRecord(
            startTime = startTime,
            endTime = endTime,
            samples = listOf(
                CyclingPedalingCadenceRecord.Sample(time = startTime, revolutionsPerMinute = 90.0)
            ),
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        val rpmUnit = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit())
        assertEquals(90.0, hk.quantity.doubleValueForUnit(rpmUnit))

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as CyclingPedalingCadenceRecord
        assertEquals(common.samples, mappedBack.samples)
    }

    @Test
    fun distanceMapping() = runTest {
        val common = DistanceRecord(
            startTime = startTime,
            endTime = endTime,
            distance = 100.meters,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(100.0, hk.quantity.doubleValueForUnit(HKUnit.meterUnit()))

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as DistanceRecord
        assertEquals(common.distance, mappedBack.distance)
    }

    @Test
    fun exerciseSessionMapping() = runTest {
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
            totalDistance = 1000.meters,
            totalEnergyBurned = 100.kilocalories,
            exerciseRoute = null
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKWorkout
        assertEquals(HKWorkoutActivityTypeRunning, hk.workoutActivityType)

        val mappedBack = hkList.filterIsInstance<HKWorkout>().toHealthRecords(HKHealthStore()).first() as ExerciseSessionRecord
        assertEquals(common.exerciseType, mappedBack.exerciseType)
        assertEquals(common.segments.size, mappedBack.segments.size)
        assertEquals(common.laps, mappedBack.laps)
        assertEquals(common.totalDistance, mappedBack.totalDistance)
        assertEquals(common.totalEnergyBurned, mappedBack.totalEnergyBurned)
    }

    @Test
    fun heartRateMapping() = runTest {
        val common = HeartRateRecord(
            startTime = startTime,
            endTime = endTime,
            samples = listOf(HeartRateRecord.Sample(time = startTime, beatsPerMinute = 70)),
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        val heartRateUnit = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit())
        assertEquals(70.0, hk.quantity.doubleValueForUnit(heartRateUnit))

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as HeartRateRecord
        assertEquals(common.samples, mappedBack.samples)
    }

    @Test
    fun heightMapping() = runTest {
        val common = HeightRecord(time = time, height = 1.8.meters, metadata = metadata)
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(1.8, hk.quantity.doubleValueForUnit(HKUnit.meterUnit()))

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as HeightRecord
        assertEquals(common.height, mappedBack.height)
    }

    @Test
    fun leanBodyMassMapping() = runTest {
        val common = LeanBodyMassRecord(time = time, mass = 70.kilograms, metadata = metadata)
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(70.0, Mass.pounds(hk.quantity.doubleValueForUnit(HKUnit.poundUnit())).inKilograms, 0.1)

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as LeanBodyMassRecord
        assertEquals(common.mass.inKilograms, mappedBack.mass.inKilograms, 0.1)
    }

    @Test
    fun menstruationFlowMapping() = runTest {
        val common = MenstruationFlowRecord(
            time = time,
            flow = MenstruationFlowRecord.Flow.Medium,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKCategorySample
        assertEquals(HKCategoryValueMenstrualFlowMedium, hk.value)

        val mappedBack = (hkList as List<HKCategorySample>).toHealthRecords().first() as MenstruationFlowRecord
        assertEquals(common.flow, mappedBack.flow)
    }

    @Test
    fun menstruationPeriodMapping() = runTest {
        val common = MenstruationPeriodRecord(
            startTime = startTime,
            endTime = endTime,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKCategorySample
        assertEquals(HKCategoryValueMenstrualFlowUnspecified, hk.value)

        val mappedBack = (hkList as List<HKCategorySample>).toHealthRecords().find { it is MenstruationPeriodRecord } as MenstruationPeriodRecord
        assertEquals(common.startTime, mappedBack.startTime)
        assertEquals(common.endTime, mappedBack.endTime)
    }

    @Test
    fun ovulationTestMapping() = runTest {
        val common = OvulationTestRecord(
            time = time,
            result = OvulationTestRecord.Result.Positive,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKCategorySample
        assertEquals(HKCategoryValueOvulationTestResultLuteinizingHormoneSurge, hk.value)

        val mappedBack = (hkList as List<HKCategorySample>).toHealthRecords().first() as OvulationTestRecord
        assertEquals(common.result, mappedBack.result)
    }

    @Test
    fun powerMapping() = runTest {
        val common = PowerRecord(
            startTime = startTime,
            endTime = endTime,
            samples = listOf(PowerRecord.Sample(time = startTime, power = 200.watts)),
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(200.0, hk.quantity.doubleValueForUnit(HKUnit.wattUnit()))

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as PowerRecord
        assertEquals(common.samples, mappedBack.samples)
    }

    @Test
    fun sexualActivityMapping() = runTest {
        val common = SexualActivityRecord(
            time = time,
            protection = SexualActivityRecord.Protection.Protected,
            metadata = metadata
        )
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKCategorySample
        assertEquals(true, hk.metadata!!["HKSexualActivityProtectionUsed"])

        val mappedBack = (hkList as List<HKCategorySample>).toHealthRecords().first() as SexualActivityRecord
        assertEquals(common.protection, mappedBack.protection)
    }

    @Test
    fun sleepSessionMapping() = runTest {
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
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKCategorySample
        assertEquals(HKCategoryValueSleepAnalysisAsleepDeep, hk.value)

        val mappedBack = (hkList as List<HKCategorySample>).toHealthRecords().first() as SleepSessionRecord
        assertEquals(common.startTime, mappedBack.startTime)
        assertEquals(common.endTime, mappedBack.endTime)
        assertEquals(common.stages, mappedBack.stages)
    }

    @Test
    fun stepsMapping() = runTest {
        val common = StepsRecord(startTime = startTime, endTime = endTime, count = 1000, metadata = metadata)
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(1000.0, hk.quantity.doubleValueForUnit(HKUnit.countUnit()))

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as StepsRecord
        assertEquals(common.count, mappedBack.count)
    }

    @Test
    fun weightMapping() = runTest {
        val common = WeightRecord(time = time, weight = 80.kilograms, metadata = metadata)
        val hkList = common.toHKObjects()!!
        val hk = hkList.first() as HKQuantitySample
        assertEquals(80.0, Mass.pounds(hk.quantity.doubleValueForUnit(HKUnit.poundUnit())).inKilograms, 0.1)

        val mappedBack = (hkList as List<HKQuantitySample>).toHealthRecord(temperaturePreference = tempPreference).first() as WeightRecord
        assertEquals(common.weight.inKilograms, mappedBack.weight.inKilograms, 0.1)
    }
}
