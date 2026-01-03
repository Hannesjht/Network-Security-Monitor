package com.security.monitor.services

import androidx.room.*
import com.security.monitor.models.ThreatAlert

@Dao
interface AlertDao {
    @Insert
    suspend fun insert(alert: ThreatAlert)
    
    @Query("SELECT * FROM threat_alerts ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<ThreatAlert>
    
    @Query("SELECT COUNT(*) FROM threat_alerts WHERE severity = 'HIGH'")
    suspend fun getCriticalCount(): Int
    
    @Query("DELETE FROM threat_alerts WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
