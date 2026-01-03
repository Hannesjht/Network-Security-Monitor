package com.security.monitor.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ThreatIntelligenceService {
    
    data class ThreatIntel(
        val ip: String,
        val abuseScore: Int = 0,
        val confidence: Int = 0,
        val isMalicious: Boolean = false,
        val totalReports: Int = 0,
        val countryCode: String = "UN"
    )
    
    suspend fun checkIP(ip: String): ThreatIntel {
        return withContext(Dispatchers.IO) {
            // Skip private IPs
            if (isPrivateIP(ip)) {
                return@withContext ThreatIntel(ip = ip, countryCode = "LOCAL")
            }
            
            // Check against known malicious IPs
            val isKnownMalicious = checkKnownMaliciousIPs(ip)
            ThreatIntel(
                ip = ip,
                isMalicious = isKnownMalicious,
                abuseScore = if (isKnownMalicious) 80 else 0
            )
        }
    }
    
    private fun checkKnownMaliciousIPs(ip: String): Boolean {
        val maliciousRanges = listOf(
            "185.220.101.", // Tor exit nodes
            "45.95.147.",    // Malicious hosting
            "80.82.77.",     // Scanning networks
            "141.98.10.",    // Exploit servers
            "91.92.240."     // C2 servers
        )
        
        return maliciousRanges.any { ip.startsWith(it) }
    }
    
    private fun isPrivateIP(ip: String): Boolean {
        return when {
            ip.startsWith("10.") -> true
            ip.startsWith("192.168.") -> true
            ip.startsWith("172.") -> {
                val second = ip.split(".")[1].toIntOrNull() ?: 0
                second in 16..31
            }
            else -> false
        }
    }
}
