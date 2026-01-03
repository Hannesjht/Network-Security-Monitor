package com.security.monitor.vpn

import android.app.*
import android.content.Intent
import android.net.VpnService
import android.os.*
import com.security.monitor.MainActivity
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class MonitorVpnService : VpnService() {
    
    companion object {
        const val ACTION_START = "START_VPN"
        const val ACTION_STOP = "STOP_VPN"
        const val NOTIFICATION_CHANNEL_ID = "network_monitor_channel"
        const val NOTIFICATION_ID = 1
    }
    
    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var packetAnalyzer: PacketAnalyzer
    
    override fun onCreate() {
        super.onCreate()
        packetAnalyzer = PacketAnalyzer(this)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startVpn()
            ACTION_STOP -> stopVpn()
        }
        return START_STICKY
    }
    
    private fun startVpn() {
        if (isRunning) return
        
        // Prepare VPN interface
        val builder = Builder()
        builder.setSession("Network Security Monitor")
        builder.setMtu(1500)
        
        // Route all traffic through VPN
        builder.addAddress("10.0.0.2", 24)
        builder.addRoute("0.0.0.0", 0)
        builder.addDnsServer("8.8.8.8")
        
        try {
            vpnInterface = builder.establish()
            isRunning = true
            
            startForeground(NOTIFICATION_ID, createNotification())
            startPacketProcessing()
            
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }
    
    private fun startPacketProcessing() {
        scope.launch {
            val inputStream = FileInputStream(vpnInterface!!.fileDescriptor)
            val outputStream = FileOutputStream(vpnInterface!!.fileDescriptor)
            val buffer = ByteBuffer.allocate(32767)
            
            while (isRunning) {
                try {
                    val length = inputStream.read(buffer.array())
                    if (length > 0) {
                        // Copy packet for analysis
                        val packetCopy = buffer.array().copyOf(length)
                        
                        // Write back to allow traffic
                        outputStream.write(buffer.array(), 0, length)
                        
                        // Analyze in background
                        launch {
                            packetAnalyzer.analyzePacket(packetCopy)
                        }
                    }
                } catch (e: Exception) {
                    break
                }
                buffer.clear()
            }
        }
    }
    
    private fun stopVpn() {
        isRunning = false
        scope.cancel()
        vpnInterface?.close()
        vpnInterface = null
        stopForeground(true)
        stopSelf()
    }
    
    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Network Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Network security monitoring is active"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
        return Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Network Security Monitor")
            .setContentText("Monitoring network traffic...")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
