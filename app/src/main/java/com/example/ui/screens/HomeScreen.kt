package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppStrings
import com.example.data.model.QuoteMood
import com.example.data.model.getStrings
import com.example.ui.components.BadgePill
import com.example.ui.components.FloatingHeartEffect
import com.example.ui.components.GlassCard
import com.example.ui.components.MascotHeader
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenSun
import com.example.ui.theme.MintCalm
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.RosePrimary
import com.example.ui.viewmodel.CoupleViewModel

@Composable
fun HomeScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coupleProfile by viewModel.coupleProfile.collectAsState()
    val streakInfo by viewModel.streakInfo.collectAsState()
    val currentQuote by viewModel.currentQuote.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val isFamilyShield by viewModel.isFamilyGlanceShield.collectAsState()
    val strings = getStrings(language)

    var showHeartParticles by remember { mutableStateOf(false) }

    // Heart pulse animation for the "Thinking of you" button
    val infiniteTransition = rememberInfiniteTransition(label = "heart_pulse")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Mascot & Couple Header
            MascotHeader(
                profile = coupleProfile,
                onToggleActiveUser = { viewModel.togglePartner() },
                isDark = isDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            // BENTO GRID ROW 1: Countdown & Streak Flame
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Next Reunion Countdown
                GlassCard(
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BadgePill(
                                text = "Reunion ✈️",
                                backgroundColor = MintCalm.copy(alpha = 0.2f),
                                textColor = if (isDark) MintCalm else Color(0xFF1B6B5D)
                            )
                            Icon(
                                imageVector = Icons.Default.Flight,
                                contentDescription = "Flight",
                                tint = RosePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${coupleProfile.daysUntilVisit}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = RosePrimary
                        )
                        Text(
                            text = "${strings.countdownDays} until ${coupleProfile.partnerBCity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.75f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = RosePrimary,
                            trackColor = RosePrimary.copy(alpha = 0.2f),
                        )
                    }
                }

                // Card 2: Streak Flame Card
                GlassCard(
                    modifier = Modifier.weight(1f),
                    isDark = isDark
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BadgePill(
                                text = "Day ${streakInfo.currentStreak} 🔥",
                                backgroundColor = FlameOrange.copy(alpha = 0.2f),
                                textColor = FlameOrange
                            )
                            if (streakInfo.isFreezeActive) {
                                Icon(
                                    imageVector = Icons.Default.AcUnit,
                                    contentDescription = "Freeze Active",
                                    tint = Color(0xFF64B5F6),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.img_streak_flame),
                            contentDescription = "Streak flame",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val isCurrentUserChecked = if (coupleProfile.currentActiveUser == coupleProfile.partnerAName) {
                            streakInfo.partnerACheckedIn
                        } else {
                            streakInfo.partnerBCheckedIn
                        }

                        if (isCurrentUserChecked) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Checked in",
                                    tint = MintCalm,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Checked-in!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MintCalm
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    showHeartParticles = true
                                    viewModel.checkInStreak()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .testTag("check_in_streak_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Check-In ❤️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Freeze button
                        if (streakInfo.hasFreezeAvailable && !streakInfo.isFreezeActive) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Use Freeze ❄️",
                                fontSize = 10.sp,
                                color = Color(0xFF64B5F6),
                                modifier = Modifier
                                    .clickable { viewModel.activateStreakFreeze() }
                                    .testTag("activate_freeze_text")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BENTO HERO CARD: "Thinking of You" Instant Heartbeat
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark,
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = strings.thinkingOfYou,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF2D2530)
                    )
                    Text(
                        text = "${strings.tapToSendHug} to ${if (coupleProfile.currentActiveUser == coupleProfile.partnerAName) coupleProfile.partnerBName else coupleProfile.partnerAName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Heart Tactile Button
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(heartScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(RosePrimary, PeachWarm)
                                )
                            )
                            .clickable {
                                showHeartParticles = true
                                viewModel.sendThinkingOfYou()
                            }
                            .testTag("thinking_of_you_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Send Heartbeat",
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Vibrates partner's phone instantly 💕",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BENTO CARD: Daily Couple Quote
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.quoteOfTheDay,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = RosePrimary
                            )
                        }

                        // Mood selector pills
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            QuoteMood.values().forEach { mood ->
                                val isSelected = currentQuote.mood == mood
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) RosePrimary else Color.Transparent)
                                        .clickable { viewModel.changeQuoteMood(mood) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mood.emoji,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "\"${currentQuote.text}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentQuote.authorOrContext,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "MYLY Quote of the Day: \"${currentQuote.text}\" 💕 Day ${coupleProfile.daysTogether} apart.")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Quote"))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Quote",
                                tint = RosePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BENTO CARD: Daily Romance Song
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Music",
                                tint = Color(0xFF1DB954),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.songOfTheDay,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        BadgePill(
                            text = "Spotify / Apple 🎵",
                            backgroundColor = Color(0xFF1DB954).copy(alpha = 0.15f),
                            textColor = Color(0xFF1DB954)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Until I Found You — Stephen Sanchez",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${strings.whyThisSong} \"Dedicated by ${coupleProfile.partnerAName}: Played at the departure gate in Tokyo.\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val musicIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/track/0T5iIrXA4p5G0RJz8r5cq3"))
                                try {
                                    context.startActivity(musicIntent)
                                } catch (e: Exception) {
                                    // fallback
                                }
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                        ) {
                            Text("Listen on Spotify", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.addNewSong(
                                    title = "Until I Found You",
                                    artist = "Stephen Sanchez",
                                    why = "Played at the departure gate in Tokyo",
                                    platform = "Spotify"
                                )
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Add to Playlist", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BENTO CARD: Shared Couple XP & Level
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${strings.sharedXp} · ${coupleProfile.levelTitle}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = RosePrimary
                        )
                        Text(
                            text = "${coupleProfile.currentXp} / ${coupleProfile.maxXp} XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { coupleProfile.currentXp.toFloat() / coupleProfile.maxXp.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldenSun,
                        trackColor = GoldenSun.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BadgePill("🏆 Game Champions", backgroundColor = Color(0x20FFC043), textColor = GoldenSun)
                        BadgePill("💌 100-Day Club", backgroundColor = RosePrimary.copy(alpha = 0.15f), textColor = RosePrimary)
                        BadgePill("🌙 Same Sky Stargazers", backgroundColor = Color(0x2090D5C7), textColor = MintCalm)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Particle heart effect
        FloatingHeartEffect(
            trigger = showHeartParticles,
            modifier = Modifier.fillMaxSize()
        )
    }
}
