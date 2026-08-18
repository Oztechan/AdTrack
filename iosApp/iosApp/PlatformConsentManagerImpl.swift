import AppTrackingTransparency
import GoogleMobileAds
import UIKit
import UserMessagingPlatform
import common

/// iOS `PlatformConsentManager` (the Kotlin interface from `common`): runs the UMP consent flow,
/// driving the shared `ConsentManagerImpl` to start the Mobile Ads SDK once ads may be requested.
class PlatformConsentManagerImpl: PlatformConsentManager {

    /// Call once after the UI is on screen (the consent form is presented over the top view controller).
    func gatherConsentThenInitializeAds() {
        // Local so the shared guard lives only for the flow (kept alive by the UMP callbacks).
        let consentManager = ConsentManagerImpl(platformConsentManager: self)
        let parameters = UMPRequestParameters()
        UMPConsentInformation.sharedInstance.requestConsentInfoUpdate(with: parameters) { _ in
            UMPConsentForm.loadAndPresentIfRequired(from: Self.topViewController()) { _ in
                consentManager.initializeAdsIfPermitted()
            }
        }
        // Returning users who already consented can start ads without waiting for the update.
        consentManager.initializeAdsIfPermitted()
    }

    func canRequestAds() -> Bool {
        UMPConsentInformation.sharedInstance.canRequestAds
    }

    func initializeAds() {
        // Request App Tracking Transparency after UMP consent (per Google guidance), then start ads.
        // The prompt only appears the first time; afterwards the completion fires with the stored
        // status. Requires NSUserTrackingUsageDescription in Info.plist.
        ATTrackingManager.requestTrackingAuthorization { _ in
            DispatchQueue.main.async {
                GADMobileAds.sharedInstance().start(completionHandler: nil)
            }
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
