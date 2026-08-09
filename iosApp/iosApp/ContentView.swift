import UIKit
import SwiftUI
import common

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    // Retained for the view's lifetime so the async consent flow survives (no static singleton).
    @State private var consentManager: ConsentManager?

    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
            .onAppear {
                guard consentManager == nil else { return }
                // Gather UMP consent, then start the Mobile Ads SDK once ads may be requested.
                let manager = ConsentManagerImpl(platformConsentManager: PlatformConsentManagerImpl())
                consentManager = manager
                manager.gatherConsentThenInitializeAds()
            }
    }
}
