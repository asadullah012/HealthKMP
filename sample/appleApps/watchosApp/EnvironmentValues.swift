import SwiftUI
import HealthKMP

extension EnvironmentValues {
    @Entry var healthManager: SwiftHealthManager = SwiftHealthManager(manager: HealthManagerFactory().createManager())
    
    @Entry var readTypes: [HealthDataType] = [
        HealthDataTypeActiveEnergyBurned(),
        HealthDataTypeBloodGlucose(),
        HealthDataTypeBloodPressure(),
        HealthDataTypeBodyTemperature(),
        HealthDataTypeDistance(),
        HealthDataTypeExercise(),
        HealthDataTypeHeartRate(),
        HealthDataTypeHeight(),
        HealthDataTypeSleep(),
        HealthDataTypeSteps(),
        HealthDataTypeWeight(),
    ]
    @Entry var writeTypes: [HealthDataType] = [
        HealthDataTypeActiveEnergyBurned(),
        HealthDataTypeBloodGlucose(),
        HealthDataTypeBloodPressure(),
        HealthDataTypeBodyTemperature(),
        HealthDataTypeDistance(),
        HealthDataTypeExercise(),
        HealthDataTypeHeartRate(),
        HealthDataTypeHeight(),
        HealthDataTypeSleep(),
        HealthDataTypeSteps(),
        HealthDataTypeWeight(),
    ]
}
