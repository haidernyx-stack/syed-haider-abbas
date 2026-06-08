package com.example.data.local

import androidx.room.*
import com.example.data.model.Project
import com.example.data.model.Clip
import com.example.data.model.TranscriptLine
import com.example.data.model.ExportedVideo
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY timestamp DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): Project?

    @Query("SELECT * FROM clips WHERE projectId = :projectId ORDER BY viralityScore DESC")
    fun getClipsForProject(projectId: Long): Flow<List<Clip>>

    @Query("SELECT * FROM clips WHERE id = :id LIMIT 1")
    suspend fun getClipById(id: Long): Clip?

    @Query("SELECT * FROM transcript_lines WHERE projectId = :projectId ORDER BY startMs ASC")
    fun getTranscriptForProject(projectId: Long): Flow<List<TranscriptLine>>

    @Query("SELECT * FROM exported_videos ORDER BY timestamp DESC")
    fun getAllExports(): Flow<List<ExportedVideo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: Clip): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClips(clips: List<Clip>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranscriptLines(lines: List<TranscriptLine>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExportedVideo(video: ExportedVideo): Long

    @Update
    suspend fun updateProject(project: Project)

    @Update
    suspend fun updateClip(clip: Clip)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM clips WHERE projectId = :projectId")
    suspend fun deleteClipsForProject(projectId: Long)

    @Query("DELETE FROM transcript_lines WHERE projectId = :projectId")
    suspend fun deleteTranscriptForProject(projectId: Long)
}
