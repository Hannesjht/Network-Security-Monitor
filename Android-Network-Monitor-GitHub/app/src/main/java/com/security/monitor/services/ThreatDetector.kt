package com.security.monitor.services

import com.security.monitor.models.TrafficData

class ThreatDetector {
    
    private val knownMaliciousIPs = setOf(
        "185.220.101.132", "45.95.147.226", "80.82.77.139"
    )
    
    private val suspiciousPorts = setOf(22, 23, 445, 3389, 4444, 6667)
    
    fun analyze(traffic: TrafficData): Double {
        var score = 0.0
        
        // 1. Check known malicious IPs
        if (knownMaliciousIPs.contains(traffic.destIP)) {
            score += 80.0
        }
        
        // 2. Check suspicious ports
        if (suspiciousPorts.contains(traffic.destPort)) {
            score += 30.0
        }
        
        // 3. Large data transfers
        if (traffic.bytesSent > 10_000_000) {
            score += 15.0
        }
        
        // 4. Add randomness for demo
        score += (0..20).random().toDouble()
        
        return score.coerceAtMost(100.0)
    }
}
