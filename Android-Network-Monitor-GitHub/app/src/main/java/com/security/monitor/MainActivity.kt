package com.security.monitor

import android.os.Bundle
import android.util.Log
import com.security.monitor.fragments.DashboardFragment
import com.security.monitor.fragments.TrafficFragment
import com.security.monitor.fragments.ThreatsFragment
import com.security.monitor.fragments.SettingsFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        
        // Get the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController
        
        // Debug: Print navigation destinations
        Log.d("Navigation", "Setting up navigation...")
        
        // Connect BottomNavigationView with NavController
        navView.setupWithNavController(navController)
        
        // Force navigation on click (debug)
        navView.setOnItemSelectedListener { item ->
            Log.d("Navigation", "Item clicked: ${item.itemId}")
            
            // Manually navigate to the destination
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    navController.navigate(R.id.nav_dashboard)
                    true
                }
                R.id.nav_traffic -> {
                    navController.navigate(R.id.nav_traffic)
                    true
                }
                R.id.nav_threats -> {
                    navController.navigate(R.id.nav_threats)
                    true
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.nav_settings)
                    true
                }
                else -> false
            }
        }
        
        Log.d("Navigation", "Navigation setup complete")
    }
}