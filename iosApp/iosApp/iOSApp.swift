import FirebaseCore
import GoogleMobileAds
import SwiftUI
import common

@main
struct iOSApp: App {
    init() {
        // Configure Firebase first so Analytics (GitLive) and Crashlytics have the default app.
        FirebaseApp.configure()
        // Route Kermit logs to Crashlytics + install the unhandled-exception hook.
        MainViewControllerKt.startCrashlytics()
        // Start Koin dependency injection once at launch.
        MainViewControllerKt.startKoin()
        // Initialize the Google Mobile Ads SDK. App ID comes from GADApplicationIdentifier in Info.plist.
        MobileAds.shared.start(completionHandler: nil)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea()
        }
    }
}
