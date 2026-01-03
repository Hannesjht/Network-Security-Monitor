package com.security.monitor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.security.monitor.R

class ThreatsFragment : Fragment() {
    
    private lateinit var threatsCountText: TextView
    private lateinit var recentThreatsText: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_threats, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        threatsCountText = view.findViewById(R.id.threats_count)
        recentThreatsText = view.findViewById(R.id.recent_threats_list)
        
        // Load threat data
        loadThreatData()
    }
    
    private fun loadThreatData() {
        // Example threat data
        threatsCountText.text = "Total Threats Detected: 3"
        
        val threatsList = """
            ⚠️ Malicious IP: 185.220.101.132
            Time: 14:30:22
            Severity: HIGH
            Action: Blocked
            
            ⚠️ Port Scan Detected
            Time: 13:45:10  
            Severity: MEDIUM
            Source: 192.168.1.105
            
            ⚠️ Suspicious DNS Query
            Time: 12:15:05
            Severity: LOW
            Domain: malware.example.com
        """.trimIndent()
        
        recentThreatsText.text = threatsList
    }
}