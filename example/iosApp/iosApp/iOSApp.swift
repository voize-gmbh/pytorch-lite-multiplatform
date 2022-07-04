import SwiftUI
import shared

@main
struct iOSApp: App {
    func copyModuleToDocumentsDir() {
        let sourceURL = Bundle.main.url(forResource: "dummy_module", withExtension: "ptl")!
        let documentsURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        let destURL = documentsURL.appendingPathComponent("dummy_module").appendingPathExtension("ptl")
        let fileManager = FileManager.default
        
        
        if !fileManager.fileExists(atPath: destURL.path) {
            try! fileManager.copyItem(at: sourceURL, to: destURL)
        }
    }
    
	var body: some Scene {
		WindowGroup {
            ContentView().onAppear {
                copyModuleToDocumentsDir()
                Inference().run()
            }
        }
	}
}
