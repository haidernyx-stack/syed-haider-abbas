package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CyberButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    color: Color = NeonPurple,
    isEnabled: Boolean = true
) {
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isEnabled) color else color.copy(alpha = 0.3f)
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (isEnabled) TextWhite else TextWhite.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    hasNeonBorder: Boolean = false,
    borderColor: Color = NeonPurple,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val borderModifier = if (hasNeonBorder) {
        Modifier.border(1.dp, Brush.horizontalGradient(listOf(borderColor, borderColor.copy(alpha = 0.2f))), shape)
    } else {
        Modifier.border(1.dp, BorderColor, shape)
    }

    Column(
        modifier = modifier
            .clip(shape)
            .background(ObsidianSurface)
            .then(borderModifier)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun NeonBorderBox(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    glowColor: Color = NeonPurple,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(ObsidianSurface)
            .border(borderWidth, Brush.sweepGradient(listOf(glowColor, glowColor.copy(alpha = 0.1f), glowColor)), shape),
        content = content
    )
}

@Composable
fun DashboardHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = TextWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            color = TextMuted,
            fontSize = 13.sp
        )
    }
}

@Composable
fun StyledScrollbar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = NeonPurple
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(BorderColor, RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(color, RoundedCornerShape(2.dp))
        )
    }
}
