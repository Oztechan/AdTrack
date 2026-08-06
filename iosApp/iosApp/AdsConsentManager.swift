import GoogleMobileAds
import UIKit
import UserMessagingPlatform
import common

/// iOS consent host: implements the shared `ConsentGateway` (the Kotlin interface from `common`)
/// over the UMP iOS SDK and drives the shared `ConsentCoordinator`, starting the Mobile Ads SDK
/// once ads may be requested.
class AdsConsentManager: ConsentGateway {
    // Retain the host + coordinator for the duration of the async consent flow.
    private static var host: AdsConsentManager?
    private var coordinator: ConsentCoordinator?

    /// Call once after the UI is on screen (the consent form is presented over the top view controller).
    static func gatherConsentThenStartAds() {
        let host = AdsConsentManager()
        Self.host = host
        let coordinator = ConsentCoordinator(
            gateway: host,
            onAdsReady: { GADMobileAds.sharedInstance().start(completionHandler: nil) }
        )
        host.coordinator = coordinator
        coordinator.gatherConsentThenInitializeAds()
    }

    func canRequestAds() -> Bool {
        UMPConsentInformation.sharedInstance.canRequestAds
    }

    func requestConsentInfoUpdate(onComplete: @escaping () -> Void) {
        let parameters = UMPRequestParameters()
        UMPConsentInformation.sharedInstance.requestConsentInfoUpdate(with: parameters) { _ in
            onComplete()
        }
    }

    func loadAndShowFormIfRequired(onComplete: @escaping () -> Void) {
        UMPConsentForm.loadAndPresentIfRequired(from: Self.topViewController()) { _ in
            onComplete()
        }
    }

    private static func topViewController() -> UIViewController? {
        // keyWindow on UIWindowScene is iOS 15+, but the app targets iOS 14.1, so find the key
        // window via the scene's windows instead.
        let scene = UIApplication.shared.connectedScenes
            .first { $0.activationState == .foregroundActive } as? UIWindowScene
        return scene?.windows.first { $0.isKeyWindow }?.rootViewController
    }
}
