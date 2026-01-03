package com.security.monitor.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "traffic_data")
data class TrafficData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceIP: String,
    val destIP: String,
    val destPort: Int,
    val protocol: String,
    val bytesSent: Long,
    val bytesReceived: Long,
    var threatScore: Double = 0.0,
    var status: String = "NORMAL",
    var countryCode: String = "UN"
)
