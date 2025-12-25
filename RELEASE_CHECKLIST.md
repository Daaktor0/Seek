# Seek Release Checklist

Pre-release verification checklist for the Seek Android app.

---

## 🔒 Privacy & Offline-First

- [ ] App launches without network connection
- [ ] All data stored locally in encrypted SQLCipher database
- [ ] No external API calls that transmit PII
- [ ] "Research Assistance" toggle respects user preference
- [ ] Analytics requires explicit opt-in
- [ ] No crash reporting without consent

---

## 📤 Export & Wipe

- [ ] **Export JSON**: Settings → Export → JSON creates valid file
- [ ] **Export CSV**: Settings → Export → CSV creates valid file
- [ ] Exported files contain all applications and milestones
- [ ] Exported files use timestamped filenames
- [ ] **Wipe All Data**: Confirmation dialog appears
- [ ] After wipe, database is empty and passphrase key is cleared
- [ ] After wipe, app returns to fresh state

---

## 💳 Entitlements & Paywall

- [ ] **Free tier**: Can add exactly 3 active applications
- [ ] 4th application attempt shows paywall
- [ ] **Subscription**: Grants 18 total slots (3 + 15)
- [ ] **Slot pack**: Adds +5 slots per purchase
- [ ] Archived applications do NOT count against limit
- [ ] Paywall shows ₹199/mo and ₹99 pack options
- [ ] "Manage subscription" links to Play Store correctly

---

## 📱 16KB Page Size Compliance

### Emulator Test
```bash
# Create Android 15 emulator with 16KB page size
# Device Manager → Create Device → Pixel 8 Pro → Android 15 (Baklava)

# Verify page size on device
adb shell getconf PAGE_SIZE
# Expected: 16384
```

- [ ] App installs on 16KB emulator without errors
- [ ] App launches and functions normally
- [ ] No native library load failures in logcat

### APK Alignment Verification
```bash
cd %ANDROID_HOME%\build-tools\35.0.0
zipalign -c -P 16 -v 4 app-release.apk
```

- [ ] `zipalign` reports "Verification successful"
- [ ] All `.so` files show "(OK)" status

---

## 🐛 Native Debug Symbols

- [ ] `ndk.debugSymbolLevel = "SYMBOL_TABLE"` in release build
- [ ] AAB uploaded to Play Console
- [ ] Play Console shows no "missing debug symbols" warning
- [ ] Crash reports show deobfuscated native stack traces

---

## 🚀 Crash-Free Startup

### Fresh Install
- [ ] Install on clean device/emulator
- [ ] App launches without crash
- [ ] No ANR dialogs
- [ ] SQLCipher initializes without error

### Upgrade Path
- [ ] Install previous version → upgrade to new version
- [ ] Existing data is preserved
- [ ] No database migration errors

### Permission Scenarios
- [ ] Launch without granting notifications permission
- [ ] Grant notifications permission at runtime
- [ ] Deny notifications permanently → app handles gracefully

### Edge Cases
- [ ] Force stop app → relaunch
- [ ] Low memory condition → app recovers
- [ ] Screen rotation during database operations

---

## 📋 Pre-Upload Checklist

- [ ] Version code incremented
- [ ] Version name updated
- [ ] `ENABLE_DEBUG_FEATURES = false` in release
- [ ] ProGuard/R8 enabled (`isMinifyEnabled = true`)
- [ ] Resource shrinking enabled (`isShrinkResources = true`)
- [ ] Signed with release keystore
- [ ] AAB generated (not APK for Play Store)

---

## 🧪 Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Release AAB (for Play Store)
./gradlew bundleRelease

# Check 16KB alignment
zipalign -c -P 16 -v 4 app/build/outputs/apk/release/app-release.apk
```

---

## ✅ Sign-Off

| Role | Name | Date | Signature |
|------|------|------|-----------|
| Developer | | | |
| QA | | | |
| Product | | | |
