package com.security.monitor.models

import com.security.monitor.App

data class AppSettings(
    val autoStart: Boolean = App.prefs.getBoolean(App.KEY_AUTO_START, false),
    val useMLDetection: Boolean = App.prefs.getBoolean(App.KEY_USE_ML, true),
    val useGeolocation: Boolean = App.prefs.getBoolean(App.KEY_USE_GEO, true),
    val useThreatIntel: Boolean = App.prefs.getBoolean(App.KEY_USE_THREAT_INTEL, true),
    val autoBlockThreats: Boolean = App.prefs.getBoolean(App.KEY_AUTO_BLOCK, false),
    val enableRootFirewall: Boolean = App.prefs.getBoolean(App.KEY_ROOT_FIREWALL, false),
    val enableNotifications: Boolean = App.prefs.getBoolean(App.KEY_NOTIFICATIONS, true),
    val notificationThreshold: Int = 70,
    val autoBlockThreshold: Int = 90,
    val updateInterval: Int = 5000,
    val maxLogDays: Int = 30
)
