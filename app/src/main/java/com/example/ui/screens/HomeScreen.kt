package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberButton
import com.example.ui.components.DashboardHeader
import com.example.ui.components.NeonCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClipForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ClipForgeViewModel,
    modifier: Modifier = Modifier,
    onNavigateToWorkstation: () -> Unit
) {
    var videoUrl by remember { mutableStateOf("") }
    var inputTitle by remember { mutableStateOf("") }
    var isMovieMode by remember { mutableStateOf(false) }
    var includeSpoilers by remember { mutableStateOf(true) }
    
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Ready preset sample videos
    val sampleVideos = listOf(
        Triple("Fintech Secrets.mp4", "https://youtube.com/watch?v=wealth_leverage_101", "How high-net worth guys decouple time from earnings using system leverage."),
        Triple("Sci-Fi Reactor Twister (Movie Clip).mp4", "local://downloads/sci_fi_reactor_twister.mp4", "Plot highlights, action twists, and reactor core explosion scene."),
        Triple("AI Agent Autonomy Podcast.mp4", "https://youtube.com/watch?v=ai_agents_future_99", "Technical walkthrough of multi-agent containers scaling workflows.")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DashboardHeader(
            title = "ClipForge Studio",
            subtitle = "Transcribe, score engagement, and cut long videos into 9:16 Shorts automatically."
        )

        // PROCESSING WORKFLOW STATUS HUD
        AnimatedVisibility(
            visible = viewModel.isProcessing,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val p = viewModel.currentProcessingProject
            if (p != null) {
                NeonCard(
                    modifier = Modifier.fillMaxWidth(),
                    hasNeonBorder = true,
                    borderColor = NeonPurple
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACTIVE PROCESSING",
                                    color = NeonPurple,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = p.title,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )
                            }
                            Text(
                                text = "${(p.progress * 100).toInt()}%",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        LinearProgressIndicator(
                            progress = { p.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NeonPurple,
                            trackColor = BorderColor,
                        )
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(DeepDarkPurple)
                                    .border(1.dp, DeepPurpleBorder, RoundedCornerShape(99.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = p.status,
                                    color = Color(0xFFEADDFF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(ObsidianSurfaceLight)
                                    .border(1.dp, BorderColor, RoundedCornerShape(99.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isMovieMode) "8 clips detected" else "12 clips detected",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // INPUT AND SOURCE OPTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // MAIN IMPORT CONSOLE
            NeonCard(
                modifier = Modifier.weight(1.3f)
            ) {
                Text(
                    text = "Import Video Content",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Paste a YouTube URL or specify an online stream.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = inputTitle,
                    onValueChange = { inputTitle = it },
                    label = { Text("Video Title") },
                    placeholder = { Text("e.g. Scaling AI Agents") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = NeonPurple,
                        unfocusedLabelColor = TextMuted
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("Paste YouTube / Video Link") },
                    placeholder = { Text("https://www.youtube.com/watch?v=...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = NeonPurple,
                        unfocusedLabelColor = TextMuted
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // PIPELINE SETTINGS FOR THIS IMPORT
                Text(
                    text = "Clipping Algorithm Mode",
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Smart Social
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (!isMovieMode) ObsidianSurfaceLight else BorderColor)
                            .border(1.5.dp, if (!isMovieMode) NeonPurple else Color.Transparent, RoundedCornerShape(16.dp))
                            .clickable { isMovieMode = false }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = if (!isMovieMode) NeonPurple else TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Smart Social", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Podcasts/Edutainment", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    // Movie Clipping Mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isMovieMode) ObsidianSurfaceLight else BorderColor)
                            .border(1.5.dp, if (isMovieMode) NeonPink else Color.Transparent, RoundedCornerShape(16.dp))
                            .clickable { isMovieMode = true }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.List, contentDescription = null, tint = if (isMovieMode) NeonPink else TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Movie Cutter", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Plot highlights & twist", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                if (isMovieMode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BorderColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auto-add Movie Spoiler Warning Card",
                            color = TextWhite,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = includeSpoilers,
                            onCheckedChange = { includeSpoilers = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NeonPink,
                                uncheckedColor = TextMuted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CyberButton(
                    onClick = {
                        focusManager.clearFocus()
                        val finalTitle = inputTitle.ifEmpty { "Viral Clips ${System.currentTimeMillis().toString().takeLast(4)}" }
                        val finalUrl = videoUrl.ifEmpty { "https://youtube.com/watch?v=custom_${System.currentTimeMillis()}" }
                        viewModel.importVideo(finalTitle, finalUrl, isMovieMode)
                        // Clear fields
                        inputTitle = ""
                        videoUrl = ""
                    },
                    text = "PROCEED AI PIPELINE",
                    icon = Icons.Default.PlayArrow,
                    color = if (isMovieMode) NeonPink else NeonPurple,
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = !viewModel.isProcessing
                )
            }

            // INSTANT PLAY PRESET DEMOS
            NeonCard(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Playable Presets & Demos",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Skip uploading! Select a preset to experience instant AI Moment extraction, 9:16 Crop previews, and Auto Captions timeline rendering.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    sampleVideos.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BorderColor)
                                .clickable(enabled = !viewModel.isProcessing) {
                                    viewModel.importVideo(
                                        title = preset.first.substringBefore("."),
                                        urlOrPath = preset.second,
                                        isMovieMode = preset.first.contains("Movie")
                                    )
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = preset.first,
                                        color = if (preset.first.contains("Movie")) NeonPink else NeonCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Inject Preset",
                                        tint = TextWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.third,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
