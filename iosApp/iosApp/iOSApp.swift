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
        // Start Koin dependency injection once at launch, registering the Swift ad impls so the shared
        // ad code can drive them (the Mobile Ads SDK is linked only into this target).
        MainViewControllerKt.startKoin(
            bannerFactory: IosBannerFactoryImpl(),
            rewardedAd: IosRewardedAd(),
            interstitialAd: IosInterstitialAd()
        )
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
