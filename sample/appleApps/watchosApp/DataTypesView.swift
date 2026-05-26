import SwiftUI

struct DataTypesView : View {
    
    let navigateToActiveEnergyBurned: () -> Void
    let navigateToBloodGlucose: () -> Void
    let navigateToBloodPressure: () -> Void
    let navigateToBodyTemperature: () -> Void
    let navigateToDistance: () -> Void
    let navigateToExercise: () -> Void
    let navigateToHeartRate: () -> Void
    let navigateToHeight: () -> Void
    let navigateToSleep: () -> Void
    let navigateToSteps: () -> Void
    let navigateToWeight: () -> Void
    
    var body: some View {
        Button("Active energy burned") {
            navigateToActiveEnergyBurned()
        }
        
        Button("Blood glucose") {
            navigateToBloodGlucose()
        }
        
        Button("Blood pressure") {
            navigateToBloodPressure()
        }
        
        Button("Body temperature") {
            navigateToBodyTemperature()
        }
        
        Button("Distance") {
            navigateToDistance()
        }
        
        Button("Exercise") {
            navigateToExercise()
        }
        
        Button("Heart rate") {
            navigateToHeartRate()
        }
        
        Button("Height") {
            navigateToHeight()
        }
        
//        Button("Sleep") {
//            navigateToSleep()
//        }
        
        Button("Steps") {
            navigateToSteps()
        }
        
        Button("Weight") {
            navigateToWeight()
        }
    }
}

#Preview {
    DataTypesView(
        navigateToActiveEnergyBurned: {},
        navigateToBloodGlucose: {},
        navigateToBloodPressure: {},
        navigateToBodyTemperature: {},
        navigateToDistance: {},
        navigateToExercise: {},
        navigateToHeartRate: {},
        navigateToHeight: {},
        navigateToSleep: {},
        navigateToSteps: {},
        navigateToWeight: {},
    )
}
