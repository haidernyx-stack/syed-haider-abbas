package com.example.api

import android.util.Log
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiApiHelper {
    private const val TAG = "GeminiApiHelper"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeTranscript(title: String, transcriptText: String): String? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.startsWith("placeholder", ignoreCase = true)) {
            Log.e(TAG, "API Key is missing or default placeholder!")
            return@withContext null
        }

        val systemInstruction = "You are ClipForge AI, an expert social media clipping AI. Your job is to analyze long transcripts and extract 3 to 6 highly engaging, viral, vertical (9:16) clips (15 to 90 seconds). For each clip, generate a highly engaging Title, an attention-grabbing Hook text overlays, precise millisecond timestamps (startMs, endMs), a virality score (0-100), engagement points (0-100), standard TikTok/Hormozi style suggestion, a CTA, and a SEO description. Output MUST be a valid JSON array matching the specified keys strictly, with no markdown code fences other than raw json."

        val prompt = """
            Video Title: "$title"
            Transcript details:
            $transcriptText
            
            Find the most emotional, high-energy, funny, or plot-twist moments. Return a JSON array where each object has these exact keys:
            - title
            - hook
            - startMs
            - endMs
            - viralityScore (between 70 and 100)
            - engagementScore (between 70 and 100)
            - captionStyle (one of: TikTok, Hormozi, MrBeast, Minimal, Gaming)
            - ctaText (short vertical CTA)
            - description
        """.trimIndent()

        // Construct request JSON
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.4)
            })
            put("systemInstruction", JSONObject().apply {
               put("parts", JSONArray().apply {
                   put(JSONObject().apply {
                       put("text", systemInstruction)
                   })
               })
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val url = "$BASE_URL?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Failed request: ${response.code} ${response.message}")
                    return@withContext null
                }
                val bodyString = response.body?.string() ?: return@withContext null
                Log.d(TAG, "Response: $bodyString")
                
                // Parse response
                val jsonObject = JSONObject(bodyString)
                val candidates = jsonObject.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val contentObj = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text")
                    }
                }
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during call", e)
            null
        }
    }
}
