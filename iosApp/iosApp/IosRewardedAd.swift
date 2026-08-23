import GoogleMobileAds
import UIKit
import common

/// iOS `PlatformRewardedAd` (the Kotlin interface from `common`): loads a `GADRewardedAd` and presents
/// it from the top view controller, reporting its lifecycle back into the shared `RewardedAdManager`.
/// Lives in the app target because the Mobile Ads SDK is linked only here.
class IosRewardedAd: NSObject, PlatformRewardedAd, GADFullScreenContentDelegate {

    // Resolved lazily so Koin is already started by the time the user taps watch.
    private lazy var manager: RewardedAdManager = MainViewControllerKt.rewardedAdManager()
    private var rewardedAd: GADRewardedAd?

    func show() {
        manager.onLoading()
        GADRewardedAd.load(
            withAdUnitID: MainViewControllerKt.rewardedAdUnitId(),
            request: GADRequest()
        ) { [weak self] ad, error in
            guard let self = self else { return }
            guard let ad = ad, error == nil else {
                self.manager.onFailed()
                return
            }
            guard let root = Self.topViewController() else {
                self.manager.onFailed()
                return
            }
            self.rewardedAd = ad
            ad.fullScreenContentDelegate = self
            ad.present(fromRootViewController: root) { [weak self] in
                self?.manager.onRewardEarned()
            }
        }
    }

    // MARK: GADFullScreenContentDelegate

    func adDidDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
        rewardedAd = nil
        manager.onFinished()
    }

    func ad(_ ad: GADFullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
        rewardedAd = nil
        manager.onFailed()
    }

    private static func topViewController() -> UIViewController? {
        // keyWindow on UIWindowScene is iOS 15+, but the app targets iOS 14.1, so find the key
        // window via the scene's windows instead (mirrors the consent flow).
        let scene = UIApplication.shared.connectedScenes
            .first { $0.activationState == .foregroundActive } as? UIWindowScene
        return scene?.windows.first { $0.isKeyWindow }?.rootViewController
    }
}
