package com.security.monitor.services

import android.content.Context
import com.security.monitor.models.ThreatAlert
import com.security.monitor.models.TrafficData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseService(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val trafficDao = database.trafficDao()
    private val alertDao = database.alertDao()
    
    suspend fun logTraffic(traffic: TrafficData) {
        withContext(Dispatchers.IO) {
            trafficDao.insert(traffic)
        }
    }
    
    suspend fun logAlert(alert: ThreatAlert) {
        withContext(Dispatchers.IO) {
            alertDao.insert(alert)
        }
    }
    
    suspend fun getRecentTraffic(limit: Int = 100): List<TrafficData> {
        return withContext(Dispatchers.IO) {
            trafficDao.getRecent(limit)
        }
    }
    
    suspend fun getRecentAlerts(limit: Int = 50): List<ThreatAlert> {
        return withContext(Dispatchers.IO) {
            alertDao.getRecent(limit)
        }
    }
}
