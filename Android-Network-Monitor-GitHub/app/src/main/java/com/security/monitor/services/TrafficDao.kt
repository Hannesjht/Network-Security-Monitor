package com.security.monitor.services

import androidx.room.*
import com.security.monitor.models.TrafficData

@Dao
interface TrafficDao {
    @Insert
    suspend fun insert(traffic: TrafficData)
    
    @Query("SELECT * FROM traffic_data ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<TrafficData>
    
    @Query("SELECT COUNT(*) FROM traffic_data WHERE threatScore > 80")
    suspend fun getMaliciousCount(): Int
    
    @Query("DELETE FROM traffic_data WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
