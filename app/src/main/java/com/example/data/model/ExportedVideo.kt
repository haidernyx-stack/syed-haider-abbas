package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exported_videos")
data class ExportedVideo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clipId: Long,
    val projectId: Long,
    val filePath: String,
    val resolution: String = "1080x1920",
    val aspectPercent: String = "9:16",
    val fileSizeBytes: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
