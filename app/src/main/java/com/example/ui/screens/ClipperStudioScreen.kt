package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Clip
import com.example.ui.components.CyberButton
import com.example.ui.components.NeonCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClipForgeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClipperStudioScreen(
    viewModel: ClipForgeViewModel,
    modifier: Modifier = Modifier
) {
    val project = viewModel.selectedProject
    val clips by viewModel.activeProjectClips.collectAsState()
    val transcript by viewModel.activeProjectTranscript.collectAsState()
    val activeClip = viewModel.selectedClip

    var activeWorkstationTab by remember { mutableStateOf("clips") } // clips, style, hooks
    val coroutineScope = rememberCoroutineScope()

    if (project == null || activeClip == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Clipper Workstation Locked", color = TextWhite, fontWeight = FontWeight.Bold)
                Text("Please go to the Projects path and select a video to load it.", color = TextMuted, fontSize = 12.sp)
            }
        }
        return
    }

    // Responsive split screen layout
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // LEFT PANE: HIGH END DUAL-FRAME PREVIEW PLAYER (9:16)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${project.title} - ${project.status}",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // VERTICAL PREVIEW PLAYER
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black)
                    .border(1.dp, BorderColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background Simulated Frames
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ObsidianSurfaceLight,
                                    SlateBg
                                )
                            )
                        )
                )

                // Face / Motion Tracking Crop bounding border (Visual Simulation)
                var boxOffsetLeft by remember { mutableStateOf(0.35f) }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5625f) // strictly 9:16 ratio bounding area
                        .align(Alignment.Center)
                        .background(Color(0xFF0A0A10))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(0.dp))
                ) {
                    // Face focus tracked rectangle
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.53f) // Crop area
                            .offset(x = 10.dp)
                            .align(Alignment.Center)
                            .border(2.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPink)), RoundedCornerShape(4.dp))
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    viewModel.updateClipCrop(
                                        centerX = (viewModel.selectedClip?.cropCenterX ?: 0.5f) + (dragAmount.x / 1000f),
                                        centerY = 0.5f,
                                        scale = 1.0f
                                    )
                                }
                            }
                    ) {
                        // Cyan corners for drag indicator
                        Box(modifier = Modifier.align(Alignment.TopStart).size(10.dp).background(NeonCyan))
                        Box(modifier = Modifier.align(Alignment.BottomEnd).size(10.dp).background(NeonPink))

                        // Active Dynamic Subtitles on vertical Canvas
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = activeClip.hook,
                                color = TextWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(4.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            // Captions based on Selected Style
                            RenderCaptionOverlay(
                                captionStyle = viewModel.editingCaptionStyle,
                                text = "WE MUST DECOUPLE TIME FROM EARNINGS!"
                            )
                        }

                        // Bottom permanent CTA overlay
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .align(Alignment.BottomCenter)
                                .background(Color.Red.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.editingCtaText.uppercase(),
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Face marker text
                    Text(
                        text = "Smart Face Tracker (Active)",
                        color = NeonCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // CONTROLS BAR: TRIM TIMELINE AND PLAYBACK
            NeonCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.isPlaying = !viewModel.isPlaying },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (viewModel.isPlaying) Icons.Default.Refresh else Icons.Default.PlayArrow,
                            contentDescription = "Playback",
                            tint = NeonPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        val duration = activeClip.endMs - activeClip.startMs
                        val relativeProgress = (viewModel.playbackPositionMs - activeClip.startMs).toFloat() / duration.coerceAtLeast(1)
                        
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formatTime(viewModel.playbackPositionMs),
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "TRIM LEN: ${formatTime(duration)}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = formatTime(activeClip.endMs),
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Slider(
                            value = relativeProgress,
                            onValueChange = { percent ->
                                val target = activeClip.startMs + (percent * duration).toLong()
                                viewModel.updatePlaybackPosition(target)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = NeonPurple,
                                activeTrackColor = NeonPurple,
                                inactiveTrackColor = BorderColor
                            )
                        )
                    }
                }
            }
        }

        // RIGHT PANE: WORKSTATION CONTROL PORTAL
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // NAVIGATION TAB SWITCHER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "clips" to "Generated Clips",
                    "style" to "Caption Themes",
                    "hooks" to "AI Hooks"
                ).forEach { tab ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeWorkstationTab == tab.first) ObsidianSurfaceLight else Color.Transparent)
                            .clickable { activeWorkstationTab = tab.first }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.second,
                            color = if (activeWorkstationTab == tab.first) NeonPurple else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // DYNAMIC ACTIONS WINDOW
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
            ) {
                when (activeWorkstationTab) {
                    "clips" -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(clips, key = { it.id }) { clip ->
                                val isClipSelected = activeClip.id == clip.id
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isClipSelected) ObsidianSurfaceLight else BorderColor)
                                        .border(
                                            1.dp,
                                            if (isClipSelected) NeonPurple else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.selectClip(clip) }
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = clip.title,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(NeonPink.copy(0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Viral Score: ${clip.viralityScore}%",
                                                    color = NeonPink,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Hook: \"${clip.hook}\"",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Timestamps: ${formatTime(clip.startMs)} - ${formatTime(clip.endMs)}",
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                            if (clip.isExported) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("EXPORTED", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "style" -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Preset Caption Styles", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            
                            val styles = listOf(
                                "TikTok" to "Yellow bubble text, heavy black stroke lines.",
                                "Hormozi" to "Cyan uppercase active highlighted word animations.",
                                "MrBeast" to "Pink bubbly card titles with fun icons.",
                                "Minimal" to "Simple lower-third clean caption block.",
                                "Gaming" to "Green neon tags for video commentary headers."
                            )

                            FlowRow(
                                maxItemsInEachRow = 2,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                styles.forEach { preset ->
                                    val isSelected = viewModel.editingCaptionStyle == preset.first
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) ObsidianSurfaceLight else BorderColor)
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonCyan else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.updateClipStyle(preset.first) }
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(preset.first, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(preset.second, color = TextMuted, fontSize = 9.sp, lineHeight = 12.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Overlay Banner Text Settings", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            
                            var tempCta by remember(viewModel.editingCtaText) { mutableStateOf(viewModel.editingCtaText) }
                            var tempHook by remember(activeClip.hook) { mutableStateOf(activeClip.hook) }

                            OutlinedTextField(
                                value = tempHook,
                                onValueChange = {
                                    tempHook = it
                                    viewModel.updateClipCtaAndHook(it, tempCta)
                                },
                                label = { Text("Top Hook Overlay Phrase") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = BorderColor
                                )
                            )

                            OutlinedTextField(
                                value = tempCta,
                                onValueChange = {
                                    tempCta = it
                                    viewModel.updateClipCtaAndHook(tempHook, it)
                                },
                                label = { Text("Bottom CTA Overlay Phrase") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = NeonPurple,
                                    unfocusedBorderColor = BorderColor
                                )
                            )
                        }
                    }

                    "hooks" -> {
                        // AI HOOK ALTERNATIVES PORTAL
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "A.I. Suggested Hook Overlays",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Select high-retention titles written automatically. It replaces the current overlay text instantly.",
                                color = TextMuted,
                                fontSize = 11.sp
                            )

                            val hookCatalog = listOf(
                                "Nobody Expected This Ending..." to "Instantly flags massive plot highlights.",
                                "This 1 Simple Lever Rich People Use" to "Drives 88% higher financial interest.",
                                "Wait Untill Secrets Exploded" to "Perfect loop for high retention reels.",
                                "The Code is Automating Yourself Out" to "Provocative tech-hook, highly viral.",
                                "He Bet It All on a Blue Wire..." to "Excellent dramatic movie highlight."
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(hookCatalog) { item ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BorderColor)
                                            .clickable {
                                                viewModel.updateClipCtaAndHook(item.first, viewModel.editingCtaText)
                                            }
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.first, color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(item.second, color = TextMuted, fontSize = 10.sp)
                                            }
                                            Icon(Icons.Default.Star, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // RENDERING AND FINAL EXPORT BUTTON
            CyberButton(
                onClick = { viewModel.exportCurrentClip() },
                text = "COMPILE & EXPORT VIDEO (MP4)",
                icon = Icons.Default.Check,
                color = NeonPurple,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RenderCaptionOverlay(captionStyle: String, text: String) {
    when (captionStyle) {
        "TikTok" -> {
            Text(
                text = text,
                color = Color.Yellow,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(4.dp))
                    .border(2.dp, Color.Yellow, RoundedCornerShape(4.dp))
                    .padding(6.dp)
            )
        }
        "Hormozi" -> {
            Text(
                text = text,
                color = NeonCyan,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
        "MrBeast" -> {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonPink)
                    .padding(8.dp)
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        "Minimal" -> {
            Text(
                text = text.lowercase(),
                color = Color.White,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        "Gaming" -> {
            Text(
                text = text,
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Black.copy(0.85f), RoundedCornerShape(2.dp))
                    .padding(6.dp)
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val secTotal = ms / 1000
    val min = secTotal / 60
    val sec = secTotal % 60
    val tenths = (ms % 1000) / 100
    return String.format("%02d:%02d.%d", min, sec, tenths)
}
