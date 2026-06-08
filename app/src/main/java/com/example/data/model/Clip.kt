package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clips")
data class Clip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val title: String,
    val hook: String,
    val startMs: Long,
    val endMs: Long,
    val captionStyle: String = "TikTok", // TikTok, Hormozi, MrBeast, Minimal, Gaming
    val viralityScore: Int = 85,
    val engagementScore: Int = 80,
    val isExported: Boolean = false,
    val cropCenterX: Float = 0.5f, // Normalized screen position for center of crop (0.0 to 1.0)
    val cropCenterY: Float = 0.5f,
    val cropScale: Float = 1.0f,
    val ctaText: String = "WATCH UNTIL THE END",
    val description: String = ""
)
