import GoogleMobileAds
import UIKit
import UserMessagingPlatform

/// Gathers UMP (GDPR) consent, then starts the Mobile Ads SDK once ads may be requested
/// (`UMPConsentInformation.canRequestAds`).
///
/// We never force consent or block on it: if the user declines, the SDK still serves
/// non-personalized / limited ads automatically from the stored consent signal.
enum AdsConsentManager {
    private static var adsStarted = false

    /// Call once after the UI is on screen (the consent form is presented over the top view controller).
    static func gatherConsentThenStartAds() {
        let parameters = UMPRequestParameters()

        UMPConsentInformation.sharedInstance.requestConsentInfoUpdate(with: parameters) { _ in
            // Consent info is up to date; show the form only if the user is required to see it.
            UMPConsentForm.loadAndPresentIfRequired(from: topViewController()) { _ in
                // Ignore the form error: either way, honour whatever consent we ended up with.
                startAdsIfPermitted()
            }
        }

        // Returning users who already consented can start ads without waiting for the update.
        startAdsIfPermitted()
    }

    private static func startAdsIfPermitted() {
        guard UMPConsentInformation.sharedInstance.canRequestAds, !adsStarted else { return }
        adsStarted = true
        GADMobileAds.sharedInstance().start(completionHandler: nil)
    }

    private static func topViewController() -> UIViewController? {
        let scene = UIApplication.shared.connectedScenes
            .first { $0.activationState == .foregroundActive } as? UIWindowScene
        return scene?.keyWindow?.rootViewController
    }
}
