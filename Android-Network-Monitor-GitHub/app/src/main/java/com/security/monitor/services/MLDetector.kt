package com.security.monitor.services

import com.security.monitor.models.TrafficData
import kotlin.math.*

class MLDetector {
    
    fun analyze(traffic: TrafficData): Double {
        var score = 0.0
        
        // 1. Port analysis
        score += analyzePort(traffic.destPort) * 0.15
        
        // 2. Protocol risk
        score += when (traffic.protocol) {
            "TCP" -> 0.1
            "UDP" -> 0.3
            "ICMP" -> 0.8
            else -> 0.5
        } * 0.15
        
        // 3. Data transfer patterns
        val totalBytes = traffic.bytesSent + traffic.bytesReceived
        score += when {
            totalBytes > 100_000_000 -> 0.9
            totalBytes > 10_000_000 -> 0.7
            totalBytes > 1_000_000 -> 0.5
            totalBytes < 100 -> 0.4
            else -> 0.3
        } * 0.10
        
        // 4. Add some randomness for demo
        score += (0..10).random() / 100.0
        
        return (score * 100).coerceIn(0.0, 100.0)
    }
    
    private fun analyzePort(port: Int): Double {
        val highRiskPorts = setOf(22, 23, 445, 3389, 5900, 6667)
        val mediumRiskPorts = setOf(21, 25, 110, 143, 587, 993, 995)
        
        return when {
            port in highRiskPorts -> 0.9
            port in mediumRiskPorts -> 0.6
            port > 49152 -> 0.3
            port < 1024 -> 0.4
            else -> 0.2
        }
    }
}
