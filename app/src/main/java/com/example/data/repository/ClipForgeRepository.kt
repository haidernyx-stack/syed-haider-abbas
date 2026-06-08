package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.model.Project
import com.example.data.model.Clip
import com.example.data.model.TranscriptLine
import com.example.data.model.ExportedVideo
import kotlinx.coroutines.flow.Flow

class ClipForgeRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()
    val allExports: Flow<List<ExportedVideo>> = projectDao.getAllExports()

    suspend fun getProjectById(id: Long): Project? {
        return projectDao.getProjectById(id)
    }

    fun getClipsForProject(projectId: Long): Flow<List<Clip>> {
        return projectDao.getClipsForProject(projectId)
    }

    suspend fun getClipById(id: Long): Clip? {
        return projectDao.getClipById(id)
    }

    fun getTranscriptForProject(projectId: Long): Flow<List<TranscriptLine>> {
        return projectDao.getTranscriptForProject(projectId)
    }

    suspend fun insertProject(project: Project): Long {
        return projectDao.insertProject(project)
    }

    suspend fun insertClip(clip: Clip): Long {
        return projectDao.insertClip(clip)
    }

    suspend fun insertClips(clips: List<Clip>) {
        projectDao.insertClips(clips)
    }

    suspend fun insertTranscriptLines(lines: List<TranscriptLine>) {
        projectDao.insertTranscriptLines(lines)
    }

    suspend fun insertExportedVideo(video: ExportedVideo): Long {
        return projectDao.insertExportedVideo(video)
    }

    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project)
    }

    suspend fun updateClip(clip: Clip) {
        projectDao.updateClip(clip)
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteClipsForProject(project.id)
        projectDao.deleteTranscriptForProject(project.id)
        projectDao.deleteProject(project)
    }
}
