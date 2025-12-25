# Seek Android App - Dependency Inventory

Generated: 2025-12-25

## Gradle Configuration Files

| File | Purpose |
|------|---------|
| `gradle/libs.versions.toml` | Version catalog: 21 versions, 25 library declarations, 6 plugins |
| `app/build.gradle.kts` | App module: applies plugins, 19 implementation dependencies |
| `build.gradle.kts` (root) | Declares plugins (apply false) |
| `settings.gradle.kts` | Configures repositories (Google, Maven Central), project name "Seek" |

---

## Direct Dependencies (from app/build.gradle.kts)

### Core Android
| Dependency | Version |
|------------|---------|
| `androidx.core:core-ktx` | 1.15.0 |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.8.7 |
| `androidx.lifecycle:lifecycle-runtime-compose` | 2.8.7 |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.8.7 |
| `androidx.activity:activity-compose` | 1.9.3 |

### Jetpack Compose (via BOM 2024.11.00)
| Dependency | Resolved Version |
|------------|------------------|
| `androidx.compose.ui:ui` | 1.7.5 |
| `androidx.compose.ui:ui-graphics` | 1.7.5 |
| `androidx.compose.ui:ui-tooling-preview` | 1.7.5 |
| `androidx.compose.material3:material3` | 1.3.1 |
| `androidx.compose.material:material-icons-extended` | 1.7.5 |

### Navigation
| Dependency | Version |
|------------|---------|
| `androidx.navigation:navigation-compose` | 2.8.4 |

### Hilt (Dependency Injection)
| Dependency | Version |
|------------|---------|
| `com.google.dagger:hilt-android` | 2.52 |
| `com.google.dagger:hilt-compiler` (ksp) | 2.52 |
| `androidx.hilt:hilt-navigation-compose` | 1.2.0 |
| `androidx.hilt:hilt-work` | 1.2.0 |
| `androidx.hilt:hilt-compiler` (ksp) | 1.2.0 |

### Room (Database)
| Dependency | Version |
|------------|---------|
| `androidx.room:room-runtime` | 2.6.1 |
| `androidx.room:room-ktx` | 2.6.1 |
| `androidx.room:room-compiler` (ksp) | 2.6.1 |

### SQLCipher (Encryption)
| Dependency | Version | Notes |
|------------|---------|-------|
| `net.zetetic:sqlcipher-android` | 4.6.1 | New library (replaces android-database-sqlcipher) |
| `androidx.sqlite:sqlite-ktx` | 2.4.0 | SQLite KTX bindings |

### WorkManager
| Dependency | Version |
|------------|---------|
| `androidx.work:work-runtime-ktx` | 2.10.0 |

### DataStore
| Dependency | Version |
|------------|---------|
| `androidx.datastore:datastore-preferences` | 1.1.1 |

### Serialization
| Dependency | Version |
|------------|---------|
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.7.3 |

---

## Resolved Transitive Highlights

### Kotlin
- `org.jetbrains.kotlin:kotlin-stdlib`: **2.0.21** (unified across all dependencies)
- `org.jetbrains.kotlinx:kotlinx-coroutines-core`: 1.7.3
- `org.jetbrains.kotlinx:kotlinx-coroutines-android`: 1.7.3

### AndroidX Core
- `androidx.annotation:annotation`: 1.8.1
- `androidx.core:core`: 1.15.0
- `androidx.arch.core:core-common`: 2.2.0
- `androidx.arch.core:core-runtime`: 2.2.0

### Lifecycle
- All lifecycle components unified at **2.8.7**

### Compose
- All Compose UI components at **1.7.5** (via BOM 2024.11.00)
- Material3 at **1.3.1**

---

## Native Libraries (.so files)

| Library | Source | ABIs | 16KB Compatible |
|---------|--------|------|-----------------|
| `libsqlcipher.so` | net.zetetic:sqlcipher-android:4.6.1 | arm64-v8a, armeabi-v7a, x86, x86_64 | ✅ Yes |
| `libdatastore_shared_counter.so` | androidx.datastore:1.1.1 | arm64-v8a, armeabi-v7a, x86, x86_64 | ✅ Yes |
| `libandroidx.graphics.path.so` | androidx.compose.ui:ui-graphics | arm64-v8a, armeabi-v7a, x86, x86_64 | ✅ Yes |

---

## Version Conflicts / Resolutions

| Dependency | Requested | Resolved | Status |
|------------|-----------|----------|--------|
| `kotlin-stdlib` | Various (1.8.x) | 2.0.21 | ✅ Upgraded |
| `annotation` | 1.0.0 - 1.8.0 | 1.8.1 | ✅ Upgraded |
| `lifecycle-*` | 2.6.1 | 2.8.7 | ✅ Upgraded |
| `core` | 1.2.0 - 1.13.1 | 1.15.0 | ✅ Upgraded |

**No duplicate or conflicting versions detected.** All transitive dependencies resolve cleanly.

---

## Build Configuration

| Property | Value |
|----------|-------|
| `compileSdk` | 35 |
| `targetSdk` | 35 |
| `minSdk` | 24 |
| `jvmTarget` | 17 |
| `AGP version` | 8.13.2 |
| `Kotlin version` | 2.0.21 |
| `KSP version` | 2.0.21-1.0.25 |
| `useLegacyPackaging` | false (16KB aligned) |

---

## Plugins Applied

| Plugin | Version |
|--------|---------|
| `com.android.application` | 8.13.2 |
| `org.jetbrains.kotlin.android` | 2.0.21 |
| `org.jetbrains.kotlin.plugin.compose` | 2.0.21 |
| `com.google.devtools.ksp` | 2.0.21-1.0.25 |
| `com.google.dagger.hilt.android` | 2.52 |
| `org.jetbrains.kotlin.plugin.serialization` | 2.0.21 |
