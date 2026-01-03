package com.security.monitor.vpn

import android.content.Context
import android.util.Log
import com.security.monitor.App
import com.security.monitor.utils.RootChecker
import java.io.DataOutputStream

class RootFirewallManager(private val context: Context) {
    
    companion object {
        private const val TAG = "RootFirewall"
        private val blockedIPs = mutableSetOf<String>()
    }
    
    fun isAvailable(): Boolean {
        return RootChecker.isRootAvailable() && 
               App.prefs.getBoolean(App.KEY_ROOT_FIREWALL, false)
    }
    
    fun blockIP(ip: String, reason: String = ""): Boolean {
        if (!isAvailable()) {
            Log.w(TAG, "Root firewall not available or not enabled")
            return false
        }
        
        if (blockedIPs.contains(ip)) {
            Log.i(TAG, "IP already blocked: $ip")
            return true
        }
        
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            
            os.writeBytes("iptables -A INPUT -s $ip -j DROP\n")
            os.writeBytes("iptables -A OUTPUT -d $ip -j DROP\n")
            os.writeBytes("exit\n")
            os.flush()
            
            val exitCode = process.waitFor()
            val success = exitCode == 0
            
            if (success) {
                blockedIPs.add(ip)
                Log.i(TAG, "Successfully blocked IP: $ip")
            } else {
                Log.e(TAG, "Failed to block IP: $ip, exit code: $exitCode")
            }
            
            success
            
        } catch (e: Exception) {
            Log.e(TAG, "Error blocking IP: ${e.message}")
            false
        }
    }
    
    fun unblockIP(ip: String): Boolean {
        if (!isAvailable()) return false
        
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            
            os.writeBytes("iptables -D INPUT -s $ip -j DROP 2>/dev/null || true\n")
            os.writeBytes("iptables -D OUTPUT -d $ip -j DROP 2>/dev/null || true\n")
            os.writeBytes("exit\n")
            os.flush()
            
            val exitCode = process.waitFor()
            val success = exitCode == 0
            
            if (success) {
                blockedIPs.remove(ip)
                Log.i(TAG, "Successfully unblocked IP: $ip")
            }
            
            success
            
        } catch (e: Exception) {
            Log.e(TAG, "Error unblocking IP: ${e.message}")
            false
        }
    }
    
    fun getBlockedIPs(): List<String> {
        return blockedIPs.toList()
    }
    
    fun isBlocked(ip: String): Boolean {
        return blockedIPs.contains(ip)
    }
}
