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
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
            .onAppear {
                // Gather UMP consent, then start the Mobile Ads SDK once ads may be requested.
                AdsConsentManager.gatherConsentThenStartAds()
            }
    }
}
