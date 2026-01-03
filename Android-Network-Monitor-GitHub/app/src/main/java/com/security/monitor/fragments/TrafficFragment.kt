package com.security.monitor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.security.monitor.R

class TrafficFragment : Fragment() {
    
    private lateinit var textPacketCount: TextView
    private lateinit var textPacketRate: TextView
    private lateinit var textThreatCount: TextView
    private lateinit var textThreatLevel: TextView
    private lateinit var textTcpCount: TextView
    private lateinit var textUdpCount: TextView
    private lateinit var textIcmpCount: TextView
    private lateinit var textOtherCount: TextView
    private lateinit var textRecentActivity: TextView
    private lateinit var textTrafficStatus: TextView
    private lateinit var btnToggleMonitoring: Button
    
    // Track monitoring state
    private var isMonitoring = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // This must match your XML file name
        return inflater.inflate(R.layout.fragment_traffic, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            // Initialize views
            initializeViews(view)
            
            // Set initial values
            setInitialValues()
            
            // Setup button click listener
            setupButtonListener()
            
            println("✅ TrafficFragment loaded successfully!")
            
        } catch (e: Exception) {
            // Show error in Logcat
            println("❌ Error in TrafficFragment: ${e.message}")
            e.printStackTrace()
            
            // Show user-friendly error
            Toast.makeText(requireContext(), "Error loading traffic view", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun initializeViews(view: View) {
        textPacketCount = view.findViewById(R.id.text_packet_count)
        textPacketRate = view.findViewById(R.id.text_packet_rate)
        textThreatCount = view.findViewById(R.id.text_threat_count)
        textThreatLevel = view.findViewById(R.id.text_threat_level)
        textTcpCount = view.findViewById(R.id.text_tcp_count)
        textUdpCount = view.findViewById(R.id.text_udp_count)
        textIcmpCount = view.findViewById(R.id.text_icmp_count)
        textOtherCount = view.findViewById(R.id.text_other_count)
        textRecentActivity = view.findViewById(R.id.text_recent_activity)
        textTrafficStatus = view.findViewById(R.id.text_traffic_status)
        btnToggleMonitoring = view.findViewById(R.id.btn_toggle_monitoring)
    }
    
    private fun setInitialValues() {
        textPacketCount.text = "0"
        textPacketRate.text = "0/sec"
        textThreatCount.text = "0"
        textThreatLevel.text = "Low"
        textTcpCount.text = "0"
        textUdpCount.text = "0"
        textIcmpCount.text = "0"
        textOtherCount.text = "0"
        textRecentActivity.text = "No activity yet. Start monitoring to see live traffic."
        textTrafficStatus.text = "Ready to monitor"
        btnToggleMonitoring.text = "▶️ Start Monitoring"
        isMonitoring = false
    }
    
    private fun setupButtonListener() {
        btnToggleMonitoring.setOnClickListener {
            toggleMonitoring()
        }
    }
    
    private fun toggleMonitoring() {
        isMonitoring = !isMonitoring
        
        if (isMonitoring) {
            // Start monitoring
            btnToggleMonitoring.text = "⏸️ Stop Monitoring"
            textTrafficStatus.text = "🔍 Monitoring active..."
            textRecentActivity.text = "Monitoring started at ${getCurrentTime()}\nWaiting for network traffic..."
            
            // Simulate some traffic data (replace with real monitoring)
            simulateTrafficData()
            
            Toast.makeText(requireContext(), "Traffic monitoring started", Toast.LENGTH_SHORT).show()
        } else {
            // Stop monitoring
            btnToggleMonitoring.text = "▶️ Start Monitoring"
            textTrafficStatus.text = "Monitoring stopped"
            textRecentActivity.text = "Monitoring stopped at ${getCurrentTime()}\n\nLast session:\n- 152 packets analyzed\n- 0 threats detected"
            
            Toast.makeText(requireContext(), "Traffic monitoring stopped", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun simulateTrafficData() {
        // Simulate random traffic data
        textPacketCount.text = "152"
        textPacketRate.text = "12/sec"
        textThreatCount.text = "0"
        textThreatLevel.text = "Low"
        textTcpCount.text = "89"
        textUdpCount.text = "42"
        textIcmpCount.text = "18"
        textOtherCount.text = "3"
        
        textRecentActivity.text = """
            ${getCurrentTime()} - 12 TCP packets from 192.168.1.100
            ${getCurrentTime()} - 8 UDP packets to 8.8.8.8
            ${getCurrentTime()} - 3 ICMP ping requests
            ${getCurrentTime()} - Connection to secure server
            ${getCurrentTime()} - 45 packets analyzed, 0 threats
        """.trimIndent()
    }
    
    private fun getCurrentTime(): String {
        return java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
    }
    
    override fun onResume() {
        super.onResume()
        println("TrafficFragment resumed")
    }
    
    override fun onPause() {
        super.onPause()
        println("TrafficFragment paused")
    }
}