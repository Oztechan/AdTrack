import GoogleMobileAds
import UIKit
import common

/// iOS `IosBannerFactory` (the Kotlin interface from `common`): builds an anchored adaptive
/// `GADBannerView` sized to the Compose width and requests an ad. Lives in the app target because
/// the Mobile Ads SDK is linked only here, not into the shared framework.
class IosBannerFactoryImpl: IosBannerFactory {

    func create(adUnitId: String, width: Double) -> UIView {
        let adSize = GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(CGFloat(width))
        let banner = GADBannerView(adSize: adSize)
        banner.adUnitID = adUnitId
        banner.rootViewController = Self.topViewController()
        banner.load(GADRequest())
        return banner
    }

    private static func topViewController() -> UIViewController? {
        // keyWindow on UIWindowScene is iOS 15+, but the app targets iOS 14.1, so find the key
        // window via the scene's windows instead (mirrors the consent flow).
        let scene = UIApplication.shared.connectedScenes
            .first { $0.activationState == .foregroundActive } as? UIWindowScene
        return scene?.windows.first { $0.isKeyWindow }?.rootViewController
    }
}
