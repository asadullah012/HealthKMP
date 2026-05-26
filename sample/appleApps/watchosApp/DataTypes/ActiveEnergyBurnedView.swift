import SwiftUI
import HealthKMP

struct ActiveEnergyBurnedView : View {
    
    @Environment(\.healthManager) private var health
    
    @State private var isLoading: Bool = false
    @State private var energy: Double = 0.0
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputEnergy: String = Double.random(in: 1..<1000).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read energy: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    Text("\(energy.formatted()) kcal for last 7 days")
                }
                
                Text("Write active energy burned")
                    .font(.caption)
                TextField("Enter energy here...", text: $inputEnergy)
                    .padding(.horizontal)
                
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write energy: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write active energy burned") {
                        writeEnergy()
                    }
                }
            }
        }
        .navigationTitle("Energy")
        .onAppear {
            loadEnergy()
        }
    }
    
    private func loadEnergy() {
        Task {
            isLoading = true
            do {
                let aggregated = try await health.aggregate(
                    startTime: Calendar.current.date(byAdding: .day, value: -7, to: Date.now)!,
                    endTime: Date.now,
                    type: HealthDataTypeActiveEnergyBurned()
                )
                energy = (aggregated as! ActiveEnergyBurnedAggregatedRecord).energy.inKilocalories
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeEnergy() {
        let energyVal = Double(inputEnergy)
        if (energyVal == nil) {
            writeError = "Can't convert energy into Double"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [ActiveEnergyBurnedRecord(
                    startTime: Calendar.current.date(byAdding: .hour, value: -1, to: Date.now)!.toKotlinInstant(),
                    endTime: Date.now.toKotlinInstant(),
                    energy: EnergyKt.kilocalories(energyVal!),
                    metadata: generateManualEntryMetadata(),
                )])
                inputEnergy = ""
                loadEnergy()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    ActiveEnergyBurnedView()
}
