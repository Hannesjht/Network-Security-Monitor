package com.security.monitor.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threat_alerts")
data class ThreatAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val description: String,
    val severity: String, // "LOW", "MEDIUM", "HIGH"
    val sourceIP: String,
    val threatType: String
)
