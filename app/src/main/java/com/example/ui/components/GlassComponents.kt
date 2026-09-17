package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.NightGlass
import com.example.ui.theme.NightSurface
import com.example.ui.theme.RosePrimary
import kotlin.random.Random

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    isDark: Boolean = false,
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val bgBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                NightSurface.copy(alpha = 0.85f),
                NightGlass.copy(alpha = 0.70f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                GlassWhite,
                Color(0xFFFFF2ED).copy(alpha = 0.90f)
            )
        )
    }

    val borderStroke = BorderStroke(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = if (isDark) {
                listOf(Color(0x40A78BFA), Color(0x15FFFFFF))
            } else {
                listOf(GlassBorderLight, Color(0x20FFFFFF))
            }
        )
    )

    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else RosePrimary.copy(alpha = 0.12f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.5f) else RosePrimary.copy(alpha = 0.15f)
            )
            .border(borderStroke, shape)
            .clip(shape),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(bgBrush)
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun FloatingHeartEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val particles = remember {
        List(12) {
            val startX = Random.nextInt(-120, 120)
            val endX = startX + Random.nextInt(-80, 80)
            val size = Random.nextInt(18, 36).sp
            val duration = Random.nextInt(1200, 2200)
            val delay = Random.nextInt(0, 300)
            Triple(startX, endX, Pair(size, Pair(duration, delay)))
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        particles.forEach { particle ->
            val (startX, endX, spec) = particle
            val (fontSize, timing) = spec
            val (duration, delay) = timing

            val offsetY = remember { Animatable(0f) }
            val offsetX = remember { Animatable(startX.toFloat()) }
            val alpha = remember { Animatable(1f) }

            LaunchedEffect(trigger) {
                offsetY.snapTo(0f)
                offsetX.snapTo(startX.toFloat())
                alpha.snapTo(1f)

                kotlinx.coroutines.delay(delay.toLong())

                kotlinx.coroutines.coroutineScope {
                    launch {
                        offsetY.animateTo(
                            targetValue = -350f,
                            animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
                        )
                    }
                    launch {
                        offsetX.animateTo(
                            targetValue = endX.toFloat(),
                            animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                        )
                    }
                    launch {
                        alpha.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = duration, easing = LinearEasing)
                        )
                    }
                }
            }

            Text(
                text = listOf("💖", "💕", "✨", "💓", "🌸").random(),
                fontSize = fontSize,
                modifier = Modifier
                    .offset { IntOffset(offsetX.value.toInt(), offsetY.value.toInt()) }
                    .padding(4.dp),
                color = Color.Unspecified.copy(alpha = alpha.value)
            )
        }
    }
}

@Composable
fun BadgePill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = RosePrimary.copy(alpha = 0.15f),
    textColor: Color = RosePrimary
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
