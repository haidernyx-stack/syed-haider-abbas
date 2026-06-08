package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiApiHelper
import com.example.data.local.AppDatabase
import com.example.data.model.Clip
import com.example.data.model.ExportedVideo
import com.example.data.model.Project
import com.example.data.model.TranscriptLine
import com.example.data.repository.ClipForgeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class ClipForgeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClipForgeRepository

    val projects: StateFlow<List<Project>>
    val exports: StateFlow<List<ExportedVideo>>

    // UI Interactive States
    var selectedProject by mutableStateOf<Project?>(null)
        private set

    var selectedClip by mutableStateOf<Clip?>(null)
        private set

    var activeProjectClips = MutableStateFlow<List<Clip>>(emptyList())
        private set

    var activeProjectTranscript = MutableStateFlow<List<TranscriptLine>>(emptyList())
        private set

    // Active Player Playback
    var playbackPositionMs by mutableStateOf(0L)
    var isPlaying by mutableStateOf(false)

    // Editing Caption style selection
    var editingCaptionStyle by mutableStateOf("TikTok")
    var editingCtaText by mutableStateOf("WATCH UNTIL NEXT LEVEL")

    // Processing Progress tracking
    var isProcessing by mutableStateOf(false)
    var currentProcessingProject by mutableStateOf<Project?>(null)

    // Analytics Counter details
    val analyticsTotalHours = MutableStateFlow(24.5f)
    val analyticsClipsCreated = MutableStateFlow(118)

    // Filter/Search parameters
    var projectSearchQuery by mutableStateOf("")

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ClipForgeRepository(database.projectDao())
        projects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        exports = repository.allExports.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed some starter project demo data if database is empty to enjoy the tool immediately
        viewModelScope.launch {
            delay(400)
            repository.allProjects.collect { list ->
                if (list.isEmpty()) {
                    seedInitialData()
                }
            }
        }
    }

    fun selectProject(project: Project?) {
        selectedProject = project
        selectedClip = null
        playbackPositionMs = 0L
        isPlaying = false

        if (project != null) {
            viewModelScope.launch {
                repository.getClipsForProject(project.id).collect { clips ->
                    activeProjectClips.value = clips
                    if (clips.isNotEmpty() && selectedClip == null) {
                        selectClip(clips.first())
                    }
                }
            }
            viewModelScope.launch {
                repository.getTranscriptForProject(project.id).collect { trans ->
                    activeProjectTranscript.value = trans
                }
            }
        } else {
            activeProjectClips.value = emptyList()
            activeProjectTranscript.value = emptyList()
        }
    }

    fun selectClip(clip: Clip?) {
        selectedClip = clip
        if (clip != null) {
            playbackPositionMs = clip.startMs
            editingCaptionStyle = clip.captionStyle
            editingCtaText = clip.ctaText
        }
        isPlaying = false
    }

    fun updateClipStyle(style: String) {
        val clip = selectedClip ?: return
        editingCaptionStyle = style
        viewModelScope.launch {
            val updated = clip.copy(captionStyle = style)
            repository.updateClip(updated)
            selectedClip = updated
            // Re-fetch clips list
            selectedProject?.let { selectProject(it) }
        }
    }

    fun updateClipCtaAndHook(hook: String, cta: String) {
        val clip = selectedClip ?: return
        editingCtaText = cta
        viewModelScope.launch {
            val updated = clip.copy(hook = hook, ctaText = cta)
            repository.updateClip(updated)
            selectedClip = updated
            selectedProject?.let { selectProject(it) }
        }
    }

    fun updateClipCrop(centerX: Float, centerY: Float, scale: Float) {
        val clip = selectedClip ?: return
        viewModelScope.launch {
            val updated = clip.copy(cropCenterX = centerX, cropCenterY = centerY, cropScale = scale)
            repository.updateClip(updated)
            selectedClip = updated
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            if (selectedProject?.id == project.id) {
                selectProject(null)
            }
            repository.deleteProject(project)
        }
    }

    fun updatePlaybackPosition(ms: Long) {
        val clip = selectedClip ?: return
        // Clamp between clip boundaries
        val boundStart = clip.startMs
        val boundEnd = clip.endMs
        playbackPositionMs = ms.coerceIn(boundStart, boundEnd)
    }

    /**
     * Imports a long video (simulate downloading files, transcribing, using Gemini for smart-clipping, cutting)
     */
    fun importVideo(title: String, urlOrPath: String, isMovieMode: Boolean) {
        viewModelScope.launch {
            isProcessing = true
            
            // 1. Create the project in database with QUEUED state
            val projectId = repository.insertProject(
                Project(
                    title = title,
                    sourceUrl = if (urlOrPath.startsWith("http")) urlOrPath else null,
                    localFilePath = if (!urlOrPath.startsWith("http")) urlOrPath else null,
                    status = "QUEUED",
                    progress = 0.05f,
                    isMovieMode = isMovieMode
                )
            )

            val initialProj = repository.getProjectById(projectId) ?: return@launch
            currentProcessingProject = initialProj
            
            // 2. Simulate progressive pipeline states
            // Stage 1: File Download & Video Analysis (2 sec)
            updateProcState(initialProj.copy(status = "DOWNLOADING", progress = 0.15f))
            delay(1500)

            // Stage 2: Audio Extraction via FFmpeg (2 sec)
            updateProcState(currentProcessingProject!!.copy(status = "EXTRACTING AUDIO", progress = 0.30f))
            delay(1500)

            // Stage 3: Whisper AI Transcription (3 sec)
            updateProcState(currentProcessingProject!!.copy(status = "TRANSCRIBING (WHISPER AI)", progress = 0.50f))
            delay(1500)

            // Generate transcript mock
            val textContent = getTopicBasedTranscript(title, isMovieMode)
            val lines = parseTranscriptToLines(projectId, textContent)
            repository.insertTranscriptLines(lines)

            // Stage 4: AI Intelligent Cognitive Clipping (3 sec)
            updateProcState(currentProcessingProject!!.copy(status = "AI MOMENT DETECTION", progress = 0.70f))
            delay(1000)

            // Call real Gemini API if available, else fall back to beautiful template moments
            var clips: List<Clip>? = null
            val rawGeminiResult = GeminiApiHelper.analyzeTranscript(title, textContent)
            if (rawGeminiResult != null) {
                try {
                    clips = parseClipsFromJson(projectId, rawGeminiResult)
                } catch (e: Exception) {
                    Log.e("ClipForgeVM", "Failed to parse Gemini response, falling back to mock clips", e)
                }
            }

            if (clips == null || clips.isEmpty()) {
                // Mock smart clips based on topic
                clips = getMockClips(projectId, isMovieMode)
            }

            repository.insertClips(clips)

            // Stage 5: Motion Tracking Reframing (YOLO / face tracking) (2 sec)
            updateProcState(currentProcessingProject!!.copy(status = "CROP & REFRAMING", progress = 0.90f))
            delay(1500)

            // Completed!
            val finalProject = currentProcessingProject!!.copy(
                status = "COMPLETED",
                progress = 1.0f,
                durationSeconds = (lines.lastOrNull()?.endMs ?: 180000L) / 1000.0
            )
            updateProcState(finalProject)
            
            // Increment statistics
            analyticsTotalHours.value += (finalProject.durationSeconds / 3600.0).toFloat()
            analyticsClipsCreated.value += clips.size

            selectProject(finalProject)
            isProcessing = false
            currentProcessingProject = null
        }
    }

    fun exportCurrentClip() {
        val clip = selectedClip ?: return
        val project = selectedProject ?: return
        viewModelScope.launch {
            // Initiate progress
            val updatedProject = project.copy(status = "EXPORT RENDER", progress = 0.8f)
            repository.updateProject(updatedProject)
            
            delay(2000) // Simulate FFmpeg video encoding, adding overlays, captions stitching
            
            // Insert finished export representation
            val randomSize = (12_000_000L..42_000_000L).random()
            val exportId = repository.insertExportedVideo(
                ExportedVideo(
                    clipId = clip.id,
                    projectId = project.id,
                    filePath = "/storage/emulated/0/Movies/ClipForge/clip_${clip.id}.mp4",
                    resolution = "1080x1920",
                    fileSizeBytes = randomSize
                )
            )

            // Mark clip as exported
            val updatedClip = clip.copy(isExported = true)
            repository.updateClip(updatedClip)
            selectedClip = updatedClip

            // Release processing hold
            repository.updateProject(project.copy(status = "COMPLETED", progress = 1.0f))
            selectedProject?.let { selectProject(it) }
        }
    }

    private suspend fun updateProcState(p: Project) {
        repository.updateProject(p)
        currentProcessingProject = p
    }

    private fun parseTranscriptToLines(projectId: Long, content: String): List<TranscriptLine> {
        val sentences = content.split(". ", "? ", "! ")
        var currentMs = 12000L
        return sentences.mapIndexed { index, sentenceText ->
            val cleanSentence = sentenceText.trim()
            val durationMs = cleanSentence.split(" ").size * 320L // roughly 320ms per word
            val line = TranscriptLine(
                projectId = projectId,
                startMs = currentMs,
                endMs = currentMs + durationMs.toLong(),
                text = cleanSentence,
                speakerName = if (index % 2 == 0) "Host" else "Guest"
            )
            currentMs += durationMs.toLong() + 200L // 200ms pause
            line
        }
    }

    private fun parseClipsFromJson(projectId: Long, jsonStr: String): List<Clip> {
        val list = mutableListOf<Clip>()
        // Trim markdown boxes
        var cleanJson = jsonStr.trim()
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.substring(7)
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.substring(0, cleanJson.length - 3)
        }
        cleanJson = cleanJson.trim()

        val jsonArray = JSONArray(cleanJson)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(
                Clip(
                    projectId = projectId,
                    title = obj.optString("title", "Engagement Highlight #$i"),
                    hook = obj.optString("hook", "Wait for the ending..."),
                    startMs = obj.optLong("startMs", 15000L),
                    endMs = obj.optLong("endMs", 45000L),
                    captionStyle = obj.optString("captionStyle", "TikTok"),
                    viralityScore = obj.optInt("viralityScore", 85),
                    engagementScore = obj.optInt("engagementScore", 80),
                    ctaText = obj.optString("ctaText", "WATCH MORE SHORTS"),
                    description = obj.optString("description", "")
                )
            )
        }
        return list
    }

    private fun getTopicBasedTranscript(title: String, isMovie: Boolean): String {
        return if (isMovie) {
            "We only have three minutes before the reactor reaches critical mass. Jack, find the keycodes! I can't find them, the database has been locked! Put on the bypass wire. It is the green one, wait! No, do not touch that red wire, it triggers the automatic override! Oh my goodness, the timer has speeded up... we have ninety seconds! Everyone hold on!"
        } else if (title.contains("money", ignoreCase = true) || title.contains("business", ignoreCase = true) || title.contains("rich", ignoreCase = true)) {
            "Most people think wealth is about your paycheck. It is absolutely not. You can make half a million dollars a year and still be broke because you have high liabilities. Wealth is about assets that generate cashflow. If you own cash-flowing real estate, index funds, or computerized systems, you are decoupled from standard salary dependency. Learn to build leverage."
        } else if (title.contains("ai", ignoreCase = true) || title.contains("tech", ignoreCase = true) || title.contains("coding", ignoreCase = true)) {
            "Artificial intelligence is fundamentally restructuring software engineering speed limits. We went from writing code line-by-line to orchestrating multiple autonomous agents in single containers. This does not mean developers become obsolete; it means the elite engineer transitions to an architectural commander. Learn to system-build."
        } else {
            "The absolute highest-priority directive in business or engineering is understanding core intent. If you build exactly what the customer asks for, with high fidelity, clean lines, and deep empathy, you succeed. Quality is not about overwhelming user views with 100 features. It is about implementing the requested scope with stunning visual precision."
        }
    }

    private fun getMockClips(projectId: Long, isMovie: Boolean): List<Clip> {
        return if (isMovie) {
            listOf(
                Clip(
                    projectId = projectId,
                    title = "The Timer is Ticking...",
                    hook = "Nobody expected this plot twist!",
                    startMs = 12000L,
                    endMs = 28000L,
                    captionStyle = "Movie",
                    viralityScore = 96,
                    engagementScore = 94,
                    ctaText = "WATCH ORIGINAL HIGHLIGHT",
                    description = "Thrilling reactor core containment scenario."
                ),
                Clip(
                    projectId = projectId,
                    title = "Never Touch the Red Wire",
                    hook = "The ending changes everything...",
                    startMs = 28000L,
                    endMs = 45000L,
                    captionStyle = "TikTok",
                    viralityScore = 89,
                    engagementScore = 91,
                    ctaText = "LIKE AND SUBSCRIBE",
                    description = "Tense decision making on wire color codes."
                )
            )
        } else {
            listOf(
                Clip(
                    projectId = projectId,
                    title = "The Truth about Wealth",
                    hook = "Stop working for a salary!",
                    startMs = 12000L,
                    endMs = 38000L,
                    captionStyle = "Hormozi",
                    viralityScore = 95,
                    engagementScore = 90,
                    ctaText = "FOLLOW ME FOR MORE",
                    description = "Why high salary doesn't equal high wealth."
                ),
                Clip(
                    projectId = projectId,
                    title = "The Leverage Secret Revealed",
                    hook = "This changed my life forever...",
                    startMs = 40000L,
                    endMs = 65000L,
                    captionStyle = "MrBeast",
                    viralityScore = 91,
                    engagementScore = 88,
                    ctaText = "CHECK COMPANION APPS",
                    description = "Understanding financial decoupling leverage assets."
                ),
                Clip(
                    projectId = projectId,
                    title = "Architectural Commander Idea",
                    hook = "The future of coding is here!",
                    startMs = 20000L,
                    endMs = 50000L,
                    captionStyle = "Gaming",
                    viralityScore = 84,
                    engagementScore = 86,
                    ctaText = "VISIT WEBSITE IN BIO",
                    description = "Transitioning from coders to strategic composers."
                )
            )
        }
    }

    private suspend fun seedInitialData() {
        // Seed standard project
        val projectId = repository.insertProject(
            Project(
                title = "The Art of Financial Freedom",
                sourceUrl = "https://www.youtube.com/watch?v=wealth_leverage_101",
                durationSeconds = 180.0,
                status = "COMPLETED",
                progress = 1.0f
            )
        )

        val transText = getTopicBasedTranscript("The Art of Financial Freedom", false)
        val lines = parseTranscriptToLines(projectId, transText)
        repository.insertTranscriptLines(lines)

        val clips = getMockClips(projectId, false)
        repository.insertClips(clips)
    }
}
