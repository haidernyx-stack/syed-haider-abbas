package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DashboardHeader
import com.example.ui.components.NeonCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.ClipForgeViewModel

private data class MetricItem(
    val title: String,
    val value: String,
    val icon: ImageVector
)

@Composable
fun AnalyticsScreen(
    viewModel: ClipForgeViewModel,
    modifier: Modifier = Modifier
) {
    val totalHours by viewModel.analyticsTotalHours.collectAsState()
    val totalClips by viewModel.analyticsClipsCreated.collectAsState()
    val exports by viewModel.exports.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DashboardHeader(
                title = "Analytics & History",
                subtitle = "Track video compression, AI scoring outputs, and local batch exports."
            )
            
            Text(
                text = "Workspace: Standard Free Tier",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(NeonCyan.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // METRICS COUNTER PANELS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val metrics = listOf(
                MetricItem("TOTAL HOURS INPUT", String.format("%.1fh", totalHours), Icons.Default.Star),
                MetricItem("VIRAL CLIPS FORGED", "$totalClips", Icons.Default.Star),
                MetricItem("LOCAL METRIC CODES", "${exports.size} mp4", Icons.Default.Check)
            )

            metrics.forEach { item ->
                NeonCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = item.title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.value, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                        Icon(imageVector = item.icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // MAIN MIDDLE ROW: WORKLOAD CHART & PROCESSING LOGS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // WORKLOAD CHART (BAR LAYOUTS)
            NeonCard(
                modifier = Modifier.weight(1.2f)
            ) {
                Text(
                    text = "Clipping Output Activity",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Weekly aggregate hours of vertical media compiled.",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                
                // Native Jetpack Compose visual bar chart (Gradients and clip centers)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val daysAndValues = listOf(
                        "MON" to 0.45f,
                        "TUE" to 0.20f,
                        "WED" to 0.85f,
                        "THU" to 0.60f,
                        "FRI" to 0.95f,
                        "SAT" to 0.15f,
                        "SUN" to 0.30f
                    )

                    daysAndValues.forEach { pair ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .fillMaxHeight(pair.second)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(NeonCyan, NeonPurple)
                                        )
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            )
                            Text(pair.first, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // EXPORT HISTORY TABLE LIST
            NeonCard(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Completed Vertical Exports",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Instant download links to ready-to-post short tracks.",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (exports.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No direct outputs compiled yet.", color = TextMuted, fontSize = 11.sp)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(exports, key = { it.id }) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BorderColor)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "clip_${item.clipId}.mp4",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = String.format("%.2f MB", item.fileSizeBytes / 1_000_000.0),
                                        color = NeonCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = item.resolution + " (${item.aspectPercent})",
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("DOWNLOAD", color = SuccessGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
