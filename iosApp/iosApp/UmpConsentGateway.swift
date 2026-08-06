import GoogleMobileAds
import UIKit
import UserMessagingPlatform
import common

/// iOS implementation of the shared `ConsentGateway` (the Kotlin interface from `common`), backed
/// by the UMP iOS SDK. Lets the shared `ConsentCoordinator` drive the same consent logic as Android.
class UmpConsentGateway: ConsentGateway {
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
