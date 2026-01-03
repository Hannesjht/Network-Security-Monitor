package com.security.monitor

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class App : Application() {
    
    companion object {
        lateinit var instance: App
        lateinit var prefs: SharedPreferences
        
        // Settings keys
        const val KEY_AUTO_START = "auto_start"
        const val KEY_USE_ML = "use_ml_detection"
        const val KEY_USE_GEO = "use_geolocation"
        const val KEY_USE_THREAT_INTEL = "use_threat_intelligence"
        const val KEY_AUTO_BLOCK = "auto_block_threats"
        const val KEY_ROOT_FIREWALL = "enable_root_firewall"
        const val KEY_NOTIFICATIONS = "enable_notifications"
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        
        // Set default preferences if first run
        if (!prefs.contains(KEY_AUTO_START)) {
            setDefaultPreferences()
        }
    }
    
    private fun setDefaultPreferences() {
        prefs.edit().apply {
            putBoolean(KEY_AUTO_START, false)
            putBoolean(KEY_USE_ML, true)
            putBoolean(KEY_USE_GEO, true)
            putBoolean(KEY_USE_THREAT_INTEL, true)
            putBoolean(KEY_AUTO_BLOCK, false)
            putBoolean(KEY_ROOT_FIREWALL, false)
            putBoolean(KEY_NOTIFICATIONS, true)
            apply()
        }
    }
}
