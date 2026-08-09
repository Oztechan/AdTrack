import GoogleMobileAds
import UIKit
import UserMessagingPlatform
import common

/// iOS `PlatformConsentManager` (the Kotlin interface from `common`) backed by the UMP iOS SDK and
/// the Mobile Ads SDK. Async steps report back through `ConsentCallback`, not closures.
class PlatformConsentManagerImpl: PlatformConsentManager {
    func canRequestAds() -> Bool {
        UMPConsentInformation.sharedInstance.canRequestAds
    }

    func requestConsentInfoUpdate(callback: ConsentCallback) {
        let parameters = UMPRequestParameters()
        UMPConsentInformation.sharedInstance.requestConsentInfoUpdate(with: parameters) { _ in
            callback.onCompleted()
        }
    }

    func loadAndShowFormIfRequired(callback: ConsentCallback) {
        UMPConsentForm.loadAndPresentIfRequired(from: Self.topViewController()) { _ in
            callback.onCompleted()
        }
    }

    func initializeAds() {
        GADMobileAds.sharedInstance().start(completionHandler: nil)
    }

    private static func topViewController() -> UIViewController? {
        // keyWindow on UIWindowScene is iOS 15+, but the app targets iOS 14.1, so find the key
        // window via the scene's windows instead.
        let scene = UIApplication.shared.connectedScenes
            .first { $0.activationState == .foregroundActive } as? UIWindowScene
        return scene?.windows.first { $0.isKeyWindow }?.rootViewController
    }
}
