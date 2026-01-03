

# Network Security Monitor - Developer Guide....


![Android](https://img.shields.io/badge/Android-8.0%2B-brightgreen)

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue)

![License](https://img.shields.io/badge/License-MIT-yellow)

![Build](https://img.shields.io/badge/Build-Passing-success)


# Project Structure


Android-Network-Monitor/
├── 📁 Developer-Code/          ← SOURCE CODE (You are here)
│   ├── 📁 app/
│   │   ├── 📁 src/main/
│   │   │   ├── 📁 java/com/security/monitor/
│   │   │   │   ├── 📁 fragments/      # UI Components
│   │   │   │   ├── 📁 services/       # Background Services
│   │   │   │   ├── 📁 models/         # Data Models
│   │   │   │   ├── 📁 utils/          # Utilities
│   │   │   │   ├── 📁 vpn/            # VPN & Network
│   │   │   │   ├── App.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── TrafficAdapter.kt
│   │   │   ├── 📁 res/                # Resources
│   │   │   └── AndroidManifest.xml
│   │   ├── build.gradle.kts
│   │   ├── proguard-rules.pro
│   │   └── lint-baseline.xml
│   ├── gradle.properties
│   └── settings.gradle
├── 📁 Build-Outputs/           ← PRE-BUILT APKs
│   └── 📁 releases/
│       └── app-release.apk
├── 📁 Documentation/           ← DOCS & SCREENSHOTS
└── 📁 Resources/               ← DESIGN ASSETS
```


# Quick Start...

# Prerequisites

-Android Studio 2022.2.1 (Flamingo) or later

-Android SDK 33+ (API Level 33)

-Java 17 or Kotlin 1.9.0

-Git (for version control)


# Installation...


# Clone the repository

git clone https://github.com/Hannesjht/Android-Network-Monitor.git


# Navigate to source code...

cd Android-Network-Monitor/Developer-Code


# Open in Android Studio...

# File → Open → Select 'Developer-Code' folder


# Building...


# Build the project...

./gradlew build


# Build debug APK

./gradlew assembleDebug

# Output: ../Build-Outputs/debug/app-debug.apk

# Build release APK

./gradlew assembleRelease

# Output: ../Build-Outputs/release/app-release.apk


# Architecture...


# MVVM Architecture Pattern


View (Fragments) → ViewModel → Repository → Data Sources (Local/Network)


# Core Components...

| Component..    | Description..           | Location..
 

| Fragments      | UI Screens              | `fragments/` 

| Services       | Background operations   | `services/` 

| Models         | Data classes            | `models/`

| Utils          | Helper classes          | `utils/` 

| VPN            | Network monitoring      | `vpn/` 



# Key Classes


| Class                    | Purpose          
     

| `MainActivity.kt`        | Main entry point, navigation 

| `DashboardFragment.kt`   | Home screen with stats 

| `TrafficFragment.kt`     | Network traffic analysis 

| `ThreatsFragment.kt`     | Security threat display 

| `SettingsFragment.kt`    | App configuration 

| `MonitorVpnService.kt`   | VPN-based monitoring 

| `NotificationHelper.kt`  | Push notifications 

| `AppDatabase.kt`         | Local database 


# Dependencies...


Main dependencies in `app/build.gradle.kts`:


dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    
    // Material Design
    implementation("com.google.android.material:material:1.10.0")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
}
```

# Testing...

# Run All Tests
bash


# Unit tests..

./gradlew test


# Instrumentation tests..

./gradlew connectedAndroidTest


# Code coverage..

./gradlew jacocoTestReport


# Test Structure..

app/src/test/          # Unit Tests
 
app/src/androidTest/   # Instrumentation Tests


# Building APKs...


# Debug APK...


./gradlew assembleDebug

# Output: ../Build-Outputs/debug/app-debug.apk


# Release APK...


# Build release APK (unsigned)

./gradlew assembleRelease

# Output: ../Build-Outputs/release/app-release-unsigned.apk


# For signed APK, configure signing in gradle.properties:

# storePassword=your_password

# keyPassword=your_password

# keyAlias=key0

# storeFile=../keystore/network-monitor.jks


# Keystore Setup..


1. Generate keystore:

keytool -genkey -v -keystore network-monitor.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias key0


2. Create `keystore.properties` in root:

storePassword=your_password
keyPassword=your_password
keyAlias=key0
storeFile=../keystore/network-monitor.jks


3. Configure in `app/build.gradle.kts`:


signingConfigs {
    create("release") {
        storeFile = file(project.properties["storeFile"] as String)
        storePassword = project.properties["storePassword"] as String
        keyAlias = project.properties["keyAlias"] as String
        keyPassword = project.properties["keyPassword"] as String
    }
}
```


# Documentation...


| Document          | Description            | Location 


| User Guide        | End-user instructions  | `../README.md` 

| API Docs          | Service interfaces     | `../Documentation/API.md` 

| Architecture      | System design          | `../Documentation/Architecture.md` 

| Screenshots       | App visuals            | `../Documentation/screenshots/` 



# Contributing...

We welcome contributions! Please follow these steps:


1. Fork the repository...


2. Create a feature branch:
   
   git checkout -b feature/amazing-feature
 
 
3. Make changes in the `Developer-Code/` directory


4. Test thoroughly:
 
   ./gradlew test connectedAndroidTest
   
   
5. Commit changes:
  
   git commit -m 'Add amazing feature'
 
   
6. Push to branch:
 
   git push origin feature/amazing-feature
  
  
7. Open a Pull Request...


### Coding Standards...

- Use Kotlin for all new code

- Follow Material Design 3 guidelines

- Use Coroutines for async operations

- Write unit tests for new features

- Document public APIs with KDoc


# Issue Reporting...


Found a bug? Please report it:


1. Check existing issues

2. Create new issue with:

   - Clear description
   
   - Steps to reproduce
   
   - Expected vs actual behavior
   
   - Screenshots if applicable
   
   - Device/OS information

# Support...

### Development Issues

- GitHub Issues: [Create Issue](https://github.com/Hannesjht/Android-Network-Monitor/issues)


# Community...

- GitHub Discussions: For Q&A

- Pull Requests: For contributions

- Wiki: For detailed guides


# License...

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.


# Developer...

Hannes Vorster
 
 Vorster Security Micro Systems
 
 GitHub: [@Hannesjht](https://github.com/Hannesjht)  
   


# Acknowledgments...


- Icons: Material Design Icons

- UI: Material Design 3

- Architecture: Android Architecture Components

- Testing: JUnit, Espresso

- Build Tools: Gradle, Android Studio


# Project Status...

| Feature           | Status        | Version 


| Core Monitoring   |  Complete     | v1.0 

| UI/UX Design      |  Complete     | v1.0 

| Notifications     |  Complete     | v1.0 

| Database          |  Complete     | v1.0 

| VPN Service       |  In Progress  | v1.1 

| ML Detection      |  Planned      | v1.2 

| Export Features   |  Planned      | v1.3 


# If you find this project useful, please give it a star!

[![Star History Chart](https://api.star-history.com/svg?repos=Hannesjht/Android-Network-Monitor&type=Date)](https://star-history.com/#Hannesjht/Android-Network-Monitor)

Happy Coding! 



# Quick Links...

- Download APK](../Build-Outputs/releases/app-release.apk)

- User Documentation](../README.md)

- Report Issues](https://github.com/Hannesjht/Android-Network-Monitor/issues)

- Discussions](https://github.com/Hannesjht/Android-Network-Monitor/discussions)

- License](../LICENSE)


Note: Pre-built APKs are available in `../Build-Outputs/` for testing and distribution.


# Benefits of This Structure...


Clean Separation       - Source code vs build outputs  

Developer Friendly     - Easy to navigate and understand  

Professional           - Shows organized project management  

Maintainable           - Easy to update and scale  

Collaboration Ready    - Clear contribution guidelines  


# Updating This README...

To update this developer guide:
1. Edit `Developer-Code/README-DEV.md`
2. Keep structure consistent
3. Update version numbers
4. Test all code blocks
5. Commit with clear message

Last Updated: January 2026 
Current Version: v1.0  
Next Release: v1.1 (VPN Enhancement)

