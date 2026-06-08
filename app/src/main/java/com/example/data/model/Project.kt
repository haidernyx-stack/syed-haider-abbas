package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val sourceUrl: String? = null,
    val localFilePath: String? = null,
    val durationSeconds: Double = 0.0,
    val status: String = "QUEUED", // QUEUED, TRANSCRIBING, CLIPPING, REFRAMING, COMPLETED, FAILED
    val progress: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val isMovieMode: Boolean = false,
    val originalWidth: Int = 1920,
    val originalHeight: Int = 1080
)
