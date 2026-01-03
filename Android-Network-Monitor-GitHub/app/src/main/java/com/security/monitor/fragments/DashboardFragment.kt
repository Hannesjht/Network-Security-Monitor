package com.security.monitor.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.security.monitor.R
import com.security.monitor.TrafficAdapter
import com.security.monitor.models.TrafficData
import com.security.monitor.vpn.MonitorVpnService
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var trafficList: RecyclerView
    private lateinit var statusText: TextView
    private lateinit var threatsText: TextView
    private lateinit var recentThreatsText: TextView

    private val trafficAdapter = TrafficAdapter()
    private var updateJob: Job? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private var threatCount = 0
    
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        initViews(view)
        setupUI()
        setupBroadcastReceiver()
    }

    private fun initViews(view: View) {
        btnStart = view.findViewById(R.id.btn_start)
        btnStop = view.findViewById(R.id.btn_stop)
        trafficList = view.findViewById(R.id.traffic_list)
        statusText = view.findViewById(R.id.status_text)
        threatsText = view.findViewById(R.id.threats_text)
        recentThreatsText = view.findViewById(R.id.recent_threats)
    }

    private fun setupUI() {
        // Setup RecyclerView
        trafficList.layoutManager = LinearLayoutManager(requireContext())
        trafficList.adapter = trafficAdapter

        // Button click listeners
        btnStart.setOnClickListener {
            startMonitoring()
        }

        btnStop.setOnClickListener {
            stopMonitoring()
        }

        // Initial state
        updateStatus("Ready to start monitoring")
        btnStop.isEnabled = false
    }

    private fun startMonitoring() {
        try {
            val intent = Intent(requireContext(), MonitorVpnService::class.java)
            intent.action = MonitorVpnService.ACTION_START
            ContextCompat.startForegroundService(requireContext(), intent)

            updateStatus("Monitoring started...")
            btnStart.isEnabled = false
            btnStop.isEnabled = true

            // Start updating UI
            updateJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    delay(2000)
                    updateTrafficList()
                }
            }
            
            // Show toast to confirm button active
            Toast.makeText(requireContext(), "Monitoring started", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopMonitoring() {
        try {
            val intent = Intent(requireContext(), MonitorVpnService::class.java)
            intent.action = MonitorVpnService.ACTION_STOP
            requireActivity().stopService(intent)

            updateStatus("Monitoring stopped")
            btnStart.isEnabled = true
            btnStop.isEnabled = false
            
            // Cancel update job
            updateJob?.cancel()
            updateJob = null
            
            Toast.makeText(requireContext(), "Monitoring stopped", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBroadcastReceiver() {
        val filter = IntentFilter("THREAT_ALERT")
        
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val title = intent?.getStringExtra("title") ?: "Unknown Threat"
                threatCount++
                updateThreatsDisplay(title)
                Toast.makeText(requireContext(), "⚠️ Threat detected: $title", Toast.LENGTH_LONG).show()
            }
        }
        
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, filter)
    }

    private fun updateTrafficList() {
        val mockTraffic = listOf(
            TrafficData(
                sourceIP = "192.168.1.100",
                destIP = "8.8.8.8",
                destPort = 443,
                protocol = "TCP",
                bytesSent = 1500,
                bytesReceived = 3000,
                threatScore = 15.0,
                status = "NORMAL"
            ),
            TrafficData(
                sourceIP = "192.168.1.100",
                destIP = "185.220.101.132",
                destPort = 445,
                protocol = "TCP",
                bytesSent = 5000,
                bytesReceived = 100,
                threatScore = 85.0,
                status = "MALICIOUS"
            ),
            TrafficData(
                sourceIP = "192.168.1.100",
                destIP = "93.184.216.34",
                destPort = 80,
                protocol = "TCP",
                bytesSent = 2500,
                bytesReceived = 15000,
                threatScore = 25.0,
                status = "NORMAL"
            )
        )

        trafficAdapter.updateData(mockTraffic)
    }

    private fun updateThreatsDisplay(newThreat: String) {
        threatsText.text = "Threats Detected: $threatCount"

        val currentText = recentThreatsText.text.toString()
        val newText = if (currentText.contains("None")) {
            "Recent Threats:\n• $newThreat"
        } else {
            val lines = currentText.split("\n")
            if (lines.size > 4) {
                (listOf(lines.first()) + lines.drop(2).take(2) + "• $newThreat").joinToString("\n")
            } else {
                "$currentText\n• $newThreat"
            }
        }

        recentThreatsText.text = newText
    }

    private fun updateStatus(message: String) {
        val timestamp = dateFormat.format(Date())
        statusText.text = "[$timestamp] $message"
    }

    override fun onDestroy() {
        updateJob?.cancel()
        try {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            // Ignore if already unregistered
        }
        stopMonitoring()
        super.onDestroy()
    }
}