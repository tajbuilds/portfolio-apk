# Astro Portfolio Android

Native Android app for Taj's portfolio API (`https://tajs.io/api/mobile/*`).

## Prerequisites

- JDK 17
- Android SDK (API 34)
- `adb` available in `PATH`

## Local Run (Step-by-step)

1. Build debug APK:

```bash
./gradlew :app:assembleDebug
```

2. Start an Android target (emulator or real device):

```bash
adb devices
```

You should see at least one line ending in `device` (for example `emulator-5554	device`).

3. Install or update app on the connected target:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

4. Open **Taj Portfolio** from the app drawer.

## Useful Commands

Run unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Build release APK:

```bash
./gradlew :app:assembleRelease
```

Build Play Store bundle (AAB):

```bash
./gradlew :app:bundleRelease
```

Outputs:

- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release.apk`
- `app/build/outputs/bundle/release/app-release.aab`

## Signing (Local or CI)

Release signing can be configured either by:

1. Local `keystore.properties` file (recommended for local release builds), or
2. Environment variables:
   - `ANDROID_STORE_FILE`
   - `ANDROID_STORE_PASSWORD`
   - `ANDROID_KEY_ALIAS`
   - `ANDROID_KEY_PASSWORD`

Create local template:

```bash
cp keystore.properties.example keystore.properties
```

If signing values are missing, release builds fall back to debug signing (useful for smoke checks only).

## Cache and Offline Behavior

- Home + Work are stale after 15 minutes.
- Work detail is stale after 1 hour.
- About + Contact are stale after 12 hours.
- Stale cached content is shown as a fallback, with an in-app sync status banner.

## CI/CD

Workflow: `.github/workflows/android.yml`

- On PR and push to `master`:
  - `:app:testDebugUnitTest`
  - `:app:assembleDebug`
- On tag `v*` or manual dispatch:
  - `:app:assembleRelease`
  - `:app:bundleRelease`
  - Upload release artifacts

Optional GitHub Action secrets for signed releases:

- `ANDROID_KEYSTORE_B64` (base64 keystore file)
- `ANDROID_STORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`
