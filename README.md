# Seek

A calm, privacy-first job application tracker for Android.

![Android](https://img.shields.io/badge/Android-24+-green)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7-blue)

## Features

- 📱 **Offline-first** - All data stored locally, works without internet
- 🔐 **Encrypted** - SQLCipher database with Android Keystore
- 🔔 **Gentle reminders** - At most one reminder + one follow-up per milestone
- 📤 **Export/Wipe** - Full data ownership with JSON/CSV export
- 🎨 **Calm UI** - Sage green palette, no red/alarm colors
- ✅ **16KB compliant** - Ready for Android 15+ devices

## Architecture

```
com.seek.app/
├── data/          # Room DB, repositories, SQLCipher
├── domain/        # Use cases, models
├── ui/            # Compose screens, theme
├── di/            # Hilt modules
└── worker/        # WorkManager reminders
```

## Tech Stack

| Component | Version |
|-----------|---------|
| Kotlin | 2.0.21 |
| Compose BOM | 2024.11.00 |
| Room | 2.6.1 |
| SQLCipher | 4.6.1 |
| Hilt | 2.52 |
| WorkManager | 2.10.0 |
| AGP | 8.13.2 |

## Build

```bash
# Debug
./gradlew assembleDebug

# Release
./gradlew assembleRelease

# AAB for Play Store
./gradlew bundleRelease
```

## Privacy

- No external API calls
- No analytics without consent
- No crash reporting without consent
- Data stays on device

## License

MIT
