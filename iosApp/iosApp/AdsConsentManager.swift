import GoogleMobileAds
import common

/// iOS composition root: wires the UMP-backed `UmpConsentGateway` to the shared `ConsentCoordinator`,
/// then starts the Mobile Ads SDK once ads may be requested.
enum AdsConsentManager {
    // Retain the coordinator for the duration of the async consent flow.
    private static var coordinator: ConsentCoordinator?

    /// Call once after the UI is on screen (the consent form is presented over the top view controller).
    static func gatherConsentThenStartAds() {
        let coordinator = ConsentCoordinator(
            gateway: UmpConsentGateway(),
            onAdsReady: { GADMobileAds.sharedInstance().start(completionHandler: nil) }
        )
        self.coordinator = coordinator
        coordinator.gatherConsentThenInitializeAds()
    }
}
