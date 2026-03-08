# Play Store Release Runbook

This file is the source of truth for shipping `com.tajbuilds.portfolio` to Google Play.

## 1) One-time setup

1. Create an upload keystore (`.jks`) and store it safely.
2. Keep the upload key in password manager + secure backup.
3. Configure local signing:

```bash
cp keystore.properties.example keystore.properties
```

Fill:

```properties
storeFile=/absolute/path/to/your-release-key.jks
storePassword=...
keyAlias=...
keyPassword=...
```

## 2) Pre-release checks

1. Bump `versionCode` and `versionName` in [app/build.gradle.kts](/c:/dev/portfolio-apk/app/build.gradle.kts).
2. Run tests:

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebugAndroidTest
```

3. Run device UI tests (optional but recommended):

```bash
./gradlew :app:connectedDebugAndroidTest
```

4. Build production bundle:

```bash
./gradlew :app:bundleRelease
```

Expected output:

```text
app/build/outputs/bundle/release/app-release.aab
```

Note: release bundle build now fails if proper release signing is missing.
For local smoke builds only (never for production), you can bypass with:

```bash
ALLOW_DEBUG_SIGNED_RELEASE=true ./gradlew :app:bundleRelease
```

## 3) Play Console checklist

Before first production release, confirm:

1. App signing: enrolled in Play App Signing with your upload key.
2. Data safety form: completed for network + contact submission data.
3. Privacy Policy URL: live and linked.
4. App content declarations: completed (target audience, ads, etc.).
5. Store listing assets:
   - App icon (512x512)
   - Feature graphic (1024x500)
   - Phone screenshots (at least 2)
   - Short + full description
6. Internal testing track:
   - Upload `app-release.aab`
   - Add testers
   - Verify install + core flows

## 4) Release flow

1. Push code + tag.
2. Upload signed AAB to internal testing first.
3. Validate:
   - Home/Work/About/Contact/Settings loads
   - Contact submit works
   - Slow network + offline fallback
4. Promote internal -> closed/open -> production.

## 5) Rollback preparedness

1. Keep previous production AAB and notes.
2. Use staged rollout for production.
3. Monitor crashes and ANRs after rollout.

