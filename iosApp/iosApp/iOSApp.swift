import FirebaseCore
import SwiftUI
import common

@main
struct iOSApp: App {
    init() {
        // Configure Firebase first so Analytics (GitLive) and Crashlytics have the default app.
        FirebaseApp.configure()
        // Route Kermit logs to Crashlytics + install the unhandled-exception hook.
        MainViewControllerKt.startCrashlytics()
        // Start Koin dependency injection once at launch, registering the Swift banner factory so the
        // shared banner composable can build GADBannerViews (the ad SDK is linked only into this target).
        MainViewControllerKt.startKoin(bannerFactory: IosBannerFactoryImpl())
        // Mobile Ads SDK init happens after UMP consent is gathered (see PlatformConsentManagerImpl,
        // triggered from ContentView), so we never request ads before consent is resolved.
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea()
        }
    }
}
