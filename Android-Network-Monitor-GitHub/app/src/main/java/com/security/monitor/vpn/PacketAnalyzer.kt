package com.security.monitor.vpn

import android.content.Context
import android.content.Intent
import com.security.monitor.models.TrafficData
import java.nio.ByteBuffer

class PacketAnalyzer(private val context: Context) {
    
    private val trafficHistory = mutableListOf<TrafficData>()
    
    fun analyzePacket(packet: ByteArray) {
        try {
            if (packet.size < 20) return // Too small to be valid IP packet
            
            // Parse IP header
            val version = (packet[0].toInt() shr 4) and 0xF
            if (version != 4) return // Only IPv4 for simplicity
            
            val protocol = packet[9].toInt() and 0xFF
            val srcIP = parseIPAddress(packet.copyOfRange(12, 16))
            val destIP = parseIPAddress(packet.copyOfRange(16, 20))
            
            // Parse TCP/UDP
            val ihl = (packet[0].toInt() and 0xF) * 4
            var destPort = 0
            
            when (protocol) {
                6 -> { // TCP
                    if (packet.size >= ihl + 20) {
                        destPort = ((packet[ihl + 2].toInt() and 0xFF) shl 8) or
                                  (packet[ihl + 3].toInt() and 0xFF)
                    }
                }
                17 -> { // UDP
                    if (packet.size >= ihl + 8) {
                        destPort = ((packet[ihl + 2].toInt() and 0xFF) shl 8) or
                                  (packet[ihl + 3].toInt() and 0xFF)
                    }
                }
            }
            
            // Create traffic data
            val traffic = TrafficData(
                sourceIP = srcIP,
                destIP = destIP,
                destPort = destPort,
                protocol = when (protocol) {
                    6 -> "TCP"
                    17 -> "UDP"
                    1 -> "ICMP"
                    else -> "OTHER"
                },
                bytesSent = packet.size.toLong(),
                bytesReceived = 0
            )
            
            // Simple threat detection
            val threatScore = calculateThreatScore(traffic)
            traffic.threatScore = threatScore
            traffic.status = when {
                threatScore > 80 -> "MALICIOUS"
                threatScore > 60 -> "SUSPICIOUS"
                else -> "NORMAL"
            }
            
            // Add to history
            synchronized(trafficHistory) {
                trafficHistory.add(traffic)
                if (trafficHistory.size > 1000) {
                    trafficHistory.removeAt(0)
                }
            }
            
            // If threat detected, create alert
            if (threatScore > 70) {
                sendAlert(threatScore, destIP, destPort)
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun calculateThreatScore(traffic: TrafficData): Double {
        var score = 0.0
        
        // Known malicious IPs
        val knownMalicious = listOf("185.220.101.", "45.95.147.", "80.82.77.")
        if (knownMalicious.any { traffic.destIP.startsWith(it) }) {
            score += 80.0
        }
        
        // Suspicious ports
        val suspiciousPorts = listOf(22, 23, 445, 3389, 4444, 6667)
        if (suspiciousPorts.contains(traffic.destPort)) {
            score += 30.0
        }
        
        // Add some randomness for demo
        score += (0..20).random().toDouble()
        
        return score.coerceAtMost(100.0)
    }
    
    private fun parseIPAddress(bytes: ByteArray): String {
        return "${bytes[0].toInt() and 0xFF}." +
               "${bytes[1].toInt() and 0xFF}." +
               "${bytes[2].toInt() and 0xFF}." +
               "${bytes[3].toInt() and 0xFF}"
    }
    
    private fun sendAlert(threatScore: Double, ip: String, port: Int) {
        val intent = Intent("THREAT_ALERT")
        intent.putExtra("title", "Suspicious Connection")
        intent.putExtra("description", "High threat score connection to $ip:$port")
        intent.putExtra("ip", ip)
        intent.putExtra("severity", if (threatScore > 80) "HIGH" else "MEDIUM")
        context.sendBroadcast(intent)
    }
    
    fun getTrafficHistory(): List<TrafficData> {
        return synchronized(trafficHistory) {
            trafficHistory.toList()
        }
    }
}
