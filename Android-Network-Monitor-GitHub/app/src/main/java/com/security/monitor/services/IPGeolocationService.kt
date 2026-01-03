package com.security.monitor.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IPGeolocationService {
    
    companion object {
        private val cache = mutableMapOf<String, IPInfo>()
    }
    
    data class IPInfo(
        val countryCode: String = "UN",
        val countryName: String = "Unknown",
        val region: String = "",
        val city: String = ""
    )
    
    suspend fun getIPInfo(ip: String): IPInfo {
        cache[ip]?.let { return it }
        
        if (isPrivateIP(ip)) {
            return IPInfo(countryCode = "LOCAL", countryName = "Local Network")
        }
        
        // Simplified - in real app, use an API
        return withContext(Dispatchers.IO) {
            val info = IPInfo(
                countryCode = when {
                    ip.startsWith("185.") -> "RU"
                    ip.startsWith("45.") -> "NL"
                    ip.startsWith("80.") -> "NL"
                    ip.startsWith("47.") -> "CN"
                    ip.startsWith("3.") -> "US"
                    else -> "UN"
                },
                countryName = when {
                    ip.startsWith("185.") -> "Russia"
                    ip.startsWith("45.") -> "Netherlands"
                    ip.startsWith("80.") -> "Netherlands"
                    ip.startsWith("47.") -> "China"
                    ip.startsWith("3.") -> "United States"
                    else -> "Unknown"
                }
            )
            cache[ip] = info
            info
        }
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
