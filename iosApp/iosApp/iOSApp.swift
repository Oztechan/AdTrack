import SwiftUI
import common

@main
struct iOSApp: App {
    init() {
        // Start Koin dependency injection once at launch.
        MainViewControllerKt.startKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea()
        }
    }
}
