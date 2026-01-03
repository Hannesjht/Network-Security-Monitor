package com.security.monitor.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.security.monitor.R
import com.security.monitor.utils.NotificationHelper 
import java.io.File

class SettingsFragment : Fragment() {
    
    private lateinit var switchAutoStart: Switch
    private lateinit var switchNotifications: Switch
    private lateinit var switchMLDetection: Switch
    private lateinit var btnSaveSettings: Button
    private lateinit var textRootStatus: TextView
    private lateinit var btnGithub: Button
    private lateinit var btnFixNotifications: Button
    private lateinit var btnTestNotification: Button  
    
    // Add notification helper
    private lateinit var notificationHelper: NotificationHelper
    
    // SharedPreferences keys
    private val PREFS_NAME = "NetworkMonitorPrefs"
    private val KEY_AUTO_START = "auto_start"
    private val KEY_NOTIFICATIONS = "notifications"
    private val KEY_ML_DETECTION = "ml_detection"
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        switchAutoStart = view.findViewById(R.id.switch_auto_start)
        switchNotifications = view.findViewById(R.id.switch_notifications)
        switchMLDetection = view.findViewById(R.id.switch_ml_detection)
        btnSaveSettings = view.findViewById(R.id.btn_save_settings)
        textRootStatus = view.findViewById(R.id.text_root_status)
        btnGithub = view.findViewById(R.id.btn_github)
        btnFixNotifications = view.findViewById(R.id.btn_fix_notifications)
        btnTestNotification = view.findViewById(R.id.btn_test_notification)  
        
        // Initialize notification helper
        notificationHelper = NotificationHelper(requireContext())
        
        // Check device root status
        textRootStatus.text = checkDeviceRoot()
        
        // Load saved settings
        loadSavedSettings()
        
        // Check notification status
        checkNotificationStatus()
        
        // Save settings button
        btnSaveSettings.setOnClickListener {
            saveSettings()
        }
        
        // GitHub button
        btnGithub.setOnClickListener {
            openGitHubProfile()
        }
        
        // Fix Notifications button
        btnFixNotifications.setOnClickListener {
            showNotificationGuide()
        }
        
        // Test Notification button 
        btnTestNotification.setOnClickListener {
            sendTestNotification()
        }
        
        // Update status when switches change
        setupSwitchListeners()
    }
    
    private fun checkDeviceRoot(): String {
        return try {
            val rootPaths = listOf(
                "/system/app/Superuser.apk",
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su"
            )
            
            val hasRootBinary = rootPaths.any { File(it).exists() }
            
            if (hasRootBinary) {
                "🔓 Device: ROOTED - Advanced features available"
            } else {
                "🔒 Device: NOT ROOTED - Standard security mode"
            }
        } catch (e: Exception) {
            "📱 Device: Security status unknown"
        }
    }
    
    private fun checkNotificationStatus() {
        val notificationsEnabled = notificationHelper.areNotificationsEnabled()
        
        if (!notificationsEnabled) {
            // Show warning and fix button
            showNotificationWarning()
        } else {
            // Notifications are enabled at system level
            btnFixNotifications.visibility = View.GONE
            switchNotifications.isEnabled = true
            switchNotifications.alpha = 1f
        }
    }
    
    private fun showNotificationWarning() {
        val currentStatus = textRootStatus.text.toString()
        val warningText = """
            |
            |⚠️ NOTIFICATION SETUP REQUIRED:
            |
            |This app needs to SEND notifications before system will allow toggle.
            |
            |1. Tap "Test Notification" button below
            |2. Allow permission if asked
            |3. System will then enable notification toggle
            |
            |After testing, go to Phone Settings → Apps → This app → Notifications
            |to enable/disable as needed.
        """.trimMargin()
        
        textRootStatus.text = currentStatus + warningText
        
        // Show the fix button
        btnFixNotifications.visibility = View.VISIBLE
        btnFixNotifications.text = "📋 View Setup Instructions"
        
        // Enable the notification switch (user can try it)
        switchNotifications.isEnabled = true
        switchNotifications.alpha = 1f
    }
    
    private fun sendTestNotification() {
        try {
            // Send a test notification
            notificationHelper.sendTestNotification()
            
            Toast.makeText(
                requireContext(),
                "✅ Test notification sent! Check your notification panel.",
                Toast.LENGTH_LONG
            ).show()
            
            // Update status
            val currentStatus = textRootStatus.text.toString()
            val updateText = """
                |
                |✅ Test notification sent!
                |
                |Now you should be able to:
                |1. See notification in your notification panel
                |2. Go to Phone Settings → Apps → This app
                |3. Tap "Notifications"
                |4. Toggle should now be available
                |
                |If toggle is still unavailable, restart phone.
            """.trimMargin()
            
            textRootStatus.text = currentStatus.split("\n\n⚠️")[0] + updateText
            
        } catch (e: SecurityException) {
            // Permission denied - need to request it
            requestNotificationPermission()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "❌ Failed to send notification: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Request permission on Android 13+
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        } else {
            // For older Android, show instructions
            showNotificationGuide()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send test notification
                sendTestNotification()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Notifications won't work.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun showNotificationGuide() {
        AlertDialog.Builder(requireContext())
            .setTitle("🔔 Setup Notifications")
            .setMessage("""
                To enable notifications for this app:
                
                FOR ANDROID 13+:
                1. System should ask for permission when you tap "Test Notification"
                2. Tap "Allow" when prompted
                3. Notifications will then work
                
                FOR OLDER ANDROID:
                1. Notifications are usually enabled by default
                2. If blocked, go to Phone Settings → Apps
                3. Find this app → Notifications
                4. Enable "Allow notifications"
                
                After setup, tap "Test Notification" to verify.
            """.trimIndent())
            .setPositiveButton("Test Now") { _, _ ->
                sendTestNotification()
            }
            .setNegativeButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .show()
    }
    
    private fun openAppSettings() {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${requireContext().packageName}")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Please manually open Settings → Apps",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun loadSavedSettings() {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        val autoStart = sharedPref.getBoolean(KEY_AUTO_START, true)
        val notifications = sharedPref.getBoolean(KEY_NOTIFICATIONS, true)
        val mlDetection = sharedPref.getBoolean(KEY_ML_DETECTION, false)
        
        switchAutoStart.isChecked = autoStart
        switchNotifications.isChecked = notifications
        switchMLDetection.isChecked = mlDetection
    }
    
    private fun saveSettings() {
        val autoStart = switchAutoStart.isChecked
        val notifications = switchNotifications.isChecked
        val mlDetection = switchMLDetection.isChecked
        
        // Save to SharedPreferences
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(KEY_AUTO_START, autoStart)
            putBoolean(KEY_NOTIFICATIONS, notifications)
            putBoolean(KEY_ML_DETECTION, mlDetection)
            apply()
        }
        
        Toast.makeText(requireContext(), "✅ Settings saved!", Toast.LENGTH_SHORT).show()
        
        // If notifications are enabled, send a confirmation
        if (notifications && notificationHelper.areNotificationsEnabled()) {
            notificationHelper.sendSecurityAlert(
                "Settings Updated",
                "Network monitoring notifications are now ${if (notifications) "enabled" else "disabled"}"
            )
        }
    }
    
    private fun setupSwitchListeners() {
        switchAutoStart.setOnCheckedChangeListener { _, _ ->
            updateStatusDisplay()
        }
        
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            updateStatusDisplay()
            
            // If enabling notifications but system blocked, show warning
            if (isChecked && !notificationHelper.areNotificationsEnabled()) {
                Toast.makeText(
                    requireContext(),
                    "⚠️ Enable system notifications first (tap 'Test Notification')",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        
        switchMLDetection.setOnCheckedChangeListener { _, _ ->
            updateStatusDisplay()
        }
    }
    
    private fun updateStatusDisplay() {
        val autoStart = switchAutoStart.isChecked
        val notifications = switchNotifications.isChecked
        val mlDetection = switchMLDetection.isChecked
        
        val systemNotifications = notificationHelper.areNotificationsEnabled()
        
        val statusText = """
            |${checkDeviceRoot()}
            |
            |System Notifications: ${if (systemNotifications) "✅ Enabled" else "❌ Disabled"}
            |
            |App Settings:
            |• Auto-start: ${if (autoStart) "✅ ON" else "❌ OFF"}
            |• Notifications: ${if (notifications) "✅ ON" else "❌ OFF"}
            |• ML Detection: ${if (mlDetection) "✅ ON" else "❌ OFF"}
            |
            |${if (!systemNotifications) "⚠️ Tap 'Test Notification' to setup system notifications" else ""}
        """.trimMargin()
        
        textRootStatus.text = statusText
    }
    
    private fun openGitHubProfile() {
        try {
            val githubUrl = "https://github.com/Hannesjht"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
            
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                copyToClipboard(githubUrl)
                Toast.makeText(
                    requireContext(),
                    "📋 GitHub link copied to clipboard!",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "⚠️ Please visit: github.com/Hannesjht",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("GitHub URL", text)
        clipboard.setPrimaryClip(clip)
    }
    
    override fun onResume() {
        super.onResume()
        // Re-check when returning to fragment
        checkNotificationStatus()
        loadSavedSettings()
    }
}