# AdMob production IDs (go-live)

Every AdMob id ships with Google's **public test id** as a default, so debug/CI builds work with no
configuration. Before shipping real ads, provide the real ids as **GitHub Actions secrets** (release
CI) and, for local release builds, in `secret.properties` (git-ignored).

## Keys

| Key | Consumed by | Test default |
| --- | --- | --- |
| `ADMOB_APP_ID` | Android manifest (`Key.ADMOB_APP_ID`) + iOS `Info.plist` `GADApplicationIdentifier` | `ca-app-pub-3940256099942544~1458002511` |
| `ADMOB_BANNER_UNIT_ID_ANDROID` / `_IOS` | `BuildKonfig.ADMOB_BANNER_UNIT_ID` | banner test unit |
| `ADMOB_REWARDED_UNIT_ID_ANDROID` / `_IOS` | `BuildKonfig.ADMOB_REWARDED_UNIT_ID` | rewarded test unit |
| `ADMOB_INTERSTITIAL_UNIT_ID_ANDROID` / `_IOS` | `BuildKonfig.ADMOB_INTERSTITIAL_UNIT_ID` | interstitial test unit |

The App ID has one value per app; the unit ids are per platform (`_ANDROID` / `_IOS`).

## How to wire the real ids

1. **AdMob console** — create the Android and iOS apps, then create one banner, rewarded, and
   interstitial ad unit per platform. Copy the App IDs (`ca-app-pub-…~…`) and unit ids (`…/…`).
2. **GitHub Actions secrets** — add each key above under repo Settings → Secrets. `release.yml`
   already passes them as env; each falls back to the test id when the secret is unset.
3. **Local release builds** — add the same keys to `secret.properties`, e.g.:
   ```properties
   ADMOB_APP_ID=ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY
   ADMOB_BANNER_UNIT_ID_ANDROID=ca-app-pub-XXXXXXXXXXXXXXXX/AAAAAAAAAA
   ADMOB_BANNER_UNIT_ID_IOS=ca-app-pub-XXXXXXXXXXXXXXXX/BBBBBBBBBB
   # …rewarded / interstitial, per platform
   ```

## Notes

- **Android App ID** flows through `secret(Key.ADMOB_APP_ID)` → `manifestPlaceholders["admobAppId"]`;
  empty env falls back to `secret.properties`, then the test default.
- **iOS App ID** is injected via the `ADMOB_APP_ID` build setting: `Config.xcconfig` holds the test
  default, `Info.plist` reads `$(ADMOB_APP_ID)`, and fastlane's `build_project` overrides it from the
  `ADMOB_APP_ID` env var when present (release only).
- **Unit ids** are read in `common/build.gradle.kts` via `secret(...)` into `BuildKonfig`; an unset
  (blank) env var falls back to the test unit.
