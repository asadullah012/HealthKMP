import SwiftUI
import HealthKMP

struct DistanceView : View {
    
    @Environment(\.healthManager) private var health
    
    @State private var isLoading: Bool = false
    @State private var distance: Double = 0.0
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputDistance: String = Double.random(in: 1..<1000).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read distance: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    Text("\(distance.formatted()) m for last 7 days")
                }
                
                Text("Write distance")
                    .font(.caption)
                TextField("Enter distance here...", text: $inputDistance)
                    .padding(.horizontal)
                
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write distance: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write distance") {
                        writeDistance()
                    }
                }
            }
        }
        .navigationTitle("Distance")
        .onAppear {
            loadDistance()
        }
    }
    
    private func loadDistance() {
        Task {
            isLoading = true
            do {
                let aggregated = try await health.aggregate(
                    startTime: Calendar.current.date(byAdding: .day, value: -7, to: Date.now)!,
                    endTime: Date.now,
                    type: HealthDataTypeDistance()
                )
                distance = (aggregated as! DistanceAggregatedRecord).distance.inMeters
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeDistance() {
        let distanceVal = Double(inputDistance)
        if (distanceVal == nil) {
            writeError = "Can't convert distance into Double"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [DistanceRecord(
                    startTime: Calendar.current.date(byAdding: .hour, value: -1, to: Date.now)!.toKotlinInstant(),
                    endTime: Date.now.toKotlinInstant(),
                    distance: LengthKt.meters(distanceVal!),
                    metadata: generateManualEntryMetadata(),
                )])
                inputDistance = ""
                loadDistance()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    DistanceView()
}
