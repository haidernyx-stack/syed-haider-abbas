package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.BuildConfig
import com.example.ui.components.CyberButton
import com.example.ui.components.DashboardHeader
import com.example.ui.components.NeonCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClipForgeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: ClipForgeViewModel,
    modifier: Modifier = Modifier
) {
    var selectedModel by remember { mutableStateOf("gemini-3.5-flash") }
    var captionLanguage by remember { mutableStateOf("English (US)") }
    var defaultCaptionStyle by remember { mutableStateOf("TikTok") }
    var systemPrompt by remember { mutableStateOf("Analyze transcription text segments, extract funny/high-energy curves, calculate virality percentage, score engagement index, and output JSON timestamps.") }

    val scrollState = rememberScrollState()

    // Read key status
    val apiKey = BuildConfig.GEMINI_API_KEY
    val isKeyConfigured = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && !apiKey.startsWith("placeholder", ignoreCase = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DashboardHeader(
            title = "Agent & System Preferences",
            subtitle = "Calibrate Whisper transcribers, select cognitive models, and review service credentials."
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LEFT PANEL: KEY CONFIG & SYSTEM PROMPT
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECRET KEY AND SECRETS PANEL STATUS
                NeonCard(
                    hasNeonBorder = isKeyConfigured,
                    borderColor = if (isKeyConfigured) SuccessGreen else WarningYellow
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("GEMINI SERVICE STATUS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isKeyConfigured) "CONNECTED & ACTIVE" else "API KEY UNRESOLVED",
                                color = if (isKeyConfigured) SuccessGreen else WarningYellow,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Icon(
                            imageVector = if (isKeyConfigured) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isKeyConfigured) SuccessGreen else WarningYellow,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isKeyConfigured) {
                            "Secured encryption key successfully linked via AI Studio Secrets Panel. Direct Whisper summaries and cognitive moments clipping are live."
                        } else {
                            "ClipForge AI uses your Gemini API credential to analyze long transcripts. Please enter your key into the secure Secrets Panel in the bottom-left of AI Studio under the name GEMINI_API_KEY."
                        },
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                // AI SYSTEM PROMPT TUNER
                NeonCard {
                    Text("AI Pipeline Prompts Tuning", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Configure specific instructions fed into the moment selection LLM.", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = systemPrompt,
                        onValueChange = { systemPrompt = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    CyberButton(
                        onClick = { },
                        text = "SAVE PROMPT CHANGES",
                        color = NeonPurple,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // RIGHT PANEL: COGNITIVE MODEL & CAPTION PREFERENCES
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // MODEL ENGINE SELECTION
                NeonCard {
                    Text("Cognitive Model Engine", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val modelEngines = listOf(
                        "gemini-3.5-flash" to "Speed optimized. Standard transcript scoring.",
                        "gemini-3.1-pro-preview" to "Reasoning heavy. Handles deep video context.",
                        "veo-3.1-generate-preview" to "Video optimized. Cinematic moment cuts."
                    )

                    modelEngines.forEach { engine ->
                        val isSel = selectedModel == engine.first
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) ObsidianSurfaceLight else BorderColor)
                                .border(1.dp, if (isSel) NeonCyan else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { selectedModel = engine.first }
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(engine.first, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(engine.second, color = TextMuted, fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // TRANSLATION & LANGUAGE INPUT
                NeonCard {
                    Text("Transcription Translator", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Select translation outputs for transcription.", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    val languages = listOf("English (US)", "Spanish (ES)", "French (FR)", "German (DE)", "Japanese (JP)")

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        languages.forEach { lang ->
                            val isSel = captionLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) NeonPurple else BorderColor)
                                    .clickable { captionLanguage = lang }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(lang, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
