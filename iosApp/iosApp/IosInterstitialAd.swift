import GoogleMobileAds
import UIKit
import common

/// iOS `PlatformInterstitialAd` (the Kotlin interface from `common`): preloads a `GADInterstitialAd`
/// and presents it from the top view controller. The loaded ad is cleared once shown/dismissed so
/// `isReady` reflects a single use. Lives in the app target because the Mobile Ads SDK is linked here.
class IosInterstitialAd: NSObject, PlatformInterstitialAd, GADFullScreenContentDelegate {

    private var interstitial: GADInterstitialAd?

    func load() {
        GADInterstitialAd.load(
            withAdUnitID: MainViewControllerKt.interstitialAdUnitId(),
            request: GADRequest()
        ) { [weak self] ad, error in
            guard let self = self else { return }
            guard let ad = ad, error == nil else {
                self.interstitial = nil
                return
            }
            ad.fullScreenContentDelegate = self
            self.interstitial = ad
        }
    }

    func isReady() -> Bool {
        interstitial != nil
    }

    func show() {
        guard let ad = interstitial, let root = Self.topViewController() else { return }
        ad.present(fromRootViewController: root)
    }

    // MARK: GADFullScreenContentDelegate

    func adDidDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
        interstitial = nil
    }

    func ad(_ ad: GADFullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
        interstitial = nil
    }

    private static func topViewController() -> UIViewController? {
        // keyWindow on UIWindowScene is iOS 15+, but the app targets iOS 14.1, so find the key
        // window via the scene's windows instead (mirrors the other ad flows).
        let scene = UIApplication.shared.connectedScenes
            .first { $0.activationState == .foregroundActive } as? UIWindowScene
        return scene?.windows.first { $0.isKeyWindow }?.rootViewController
    }
}
