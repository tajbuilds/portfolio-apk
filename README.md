# Astro Portfolio Android

Native Android app for Taj's portfolio mobile API.

## Run (Debug)

```bash
./gradlew :app:assembleDebug
```

Install APK:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Release Signing Setup

Use one of these options:

1. `keystore.properties` (recommended for local builds)
2. Environment variables (`ANDROID_STORE_FILE`, `ANDROID_STORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`)

Create local file from template:

```bash
cp keystore.properties.example keystore.properties
```

## Build Release Artifacts

Signed APK:

```bash
./gradlew :app:assembleRelease
```

Play AAB:

```bash
./gradlew :app:bundleRelease
```

Output paths:

- `app/build/outputs/apk/release/`
- `app/build/outputs/bundle/release/`

If release signing values are not configured, release build falls back to debug signing so CI/local verification can still run.
