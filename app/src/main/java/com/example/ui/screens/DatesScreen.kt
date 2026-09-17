package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.DateIdeaEntity
import com.example.data.model.getStrings
import com.example.ui.components.BadgePill
import com.example.ui.components.GlassCard
import com.example.ui.theme.GoldenSun
import com.example.ui.theme.MintCalm
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.RosePrimary
import com.example.ui.viewmodel.CoupleViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DatesScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val allIdeas by viewModel.allDateIdeas.collectAsState()
    val bucketList by viewModel.bucketListIdeas.collectAsState()
    val isGenerating by viewModel.isGeneratingIdeas.collectAsState()
    val savingsSaved by viewModel.savingsSaved.collectAsState()
    val savingsGoal by viewModel.savingsGoal.collectAsState()
    val strings = getStrings(language)

    var isVirtualMode by remember { mutableStateOf(true) }
    var selectedBudget by remember { mutableStateOf("Free") }
    var selectedInterests by remember { mutableStateOf("Cooking, Stargazing, Films") }
    var customInterestInput by remember { mutableStateOf("") }
    var showBucketListOnly by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Graphic
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_dates),
                contentDescription = "Romantic Date Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = strings.aiDateIdeasTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Curated & AI-generated romantic moments across distance ✨",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TRIP SAVINGS FUND CARD
        GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FlightTakeoff, contentDescription = "Savings", tint = RosePrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.tripSavingsTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "$$savingsSaved / $$savingsGoal",
                        fontWeight = FontWeight.ExtraBold,
                        color = RosePrimary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { savingsSaved.toFloat() / savingsGoal.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MintCalm,
                    trackColor = MintCalm.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Next reunion flight fund ✈️",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BadgePill(
                            text = "+ $20 💵",
                            backgroundColor = MintCalm.copy(alpha = 0.2f),
                            textColor = MintCalm,
                            modifier = Modifier.clickable { viewModel.addSavingsContribution(20) }
                        )
                        BadgePill(
                            text = "+ $50 💰",
                            backgroundColor = GoldenSun.copy(alpha = 0.2f),
                            textColor = GoldenSun,
                            modifier = Modifier.clickable { viewModel.addSavingsContribution(50) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI DATE GENERATOR CONFIGURATOR
        GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customize Your Date Vibe",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = RosePrimary
                    )

                    // Mode Toggle: Virtual Call vs In-Person Visit
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0x33FFFFFF) else Color(0x22F39682))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isVirtualMode) RosePrimary else Color.Transparent)
                                .clickable { isVirtualMode = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Virtual 💻",
                                fontSize = 11.sp,
                                fontWeight = if (isVirtualMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (isVirtualMode) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isVirtualMode) RosePrimary else Color.Transparent)
                                .clickable { isVirtualMode = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Visit ✈️",
                                fontSize = 11.sp,
                                fontWeight = if (!isVirtualMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isVirtualMode) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Budget Chips
                Text(text = "Budget:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Free", "$", "$$", "$$$").forEach { b ->
                        val isSelected = selectedBudget == b
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) RosePrimary else if (isDark) Color(0x22FFFFFF) else Color(0x22F39682))
                                .clickable { selectedBudget = b }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = b,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interests selection
                Text(text = "Couple Interests:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val interestPool = listOf("🍝 Cooking", "🌙 Stargazing", "🎬 Movies", "🎮 Gaming", "🎨 Art & Museum", "💌 Letter Writing", "🎵 Music Jam")
                    interestPool.forEach { item ->
                        val isSelected = selectedInterests.contains(item)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) RosePrimary.copy(alpha = 0.2f) else if (isDark) Color(0x11FFFFFF) else Color(0x11F39682))
                                .border(1.dp, if (isSelected) RosePrimary else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedInterests = if (isSelected) {
                                        selectedInterests.replace(item, "").trim()
                                    } else {
                                        "$selectedInterests, $item".trim().removePrefix(",")
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = item, fontSize = 11.sp, color = if (isSelected) RosePrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons: Generate Ideas & Surprise Me
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            viewModel.generateDateIdeas(
                                interests = selectedInterests.ifBlank { "LDR Romance & Fun" },
                                budget = selectedBudget,
                                isVirtual = isVirtualMode
                            )
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(42.dp)
                            .testTag("generate_ideas_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.generateIdeasBtn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.surpriseMeDate() },
                        modifier = Modifier
                            .weight(0.9f)
                            .height(42.dp)
                            .testTag("surprise_me_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(strings.surpriseMeBtn, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle: All Ideas vs Shared Bucket List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showBucketListOnly) "Shared Bucket List 📌" else "Fresh Date Ideas 💡",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (showBucketListOnly) "Show All Ideas" else "View Bucket List (${bucketList.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RosePrimary,
                modifier = Modifier.clickable { showBucketListOnly = !showBucketListOnly }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        val displayList = if (showBucketListOnly) bucketList else allIdeas
        if (displayList.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (showBucketListOnly) "No dates in bucket list yet! Tap bookmark on any date idea to save." else "Tap 'Generate Tailored Dates' above to craft magic!",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            displayList.forEach { idea ->
                DateIdeaCard(
                    idea = idea,
                    isDark = isDark,
                    onToggleBucket = { viewModel.toggleBucketList(idea) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DateIdeaCard(
    idea: DateIdeaEntity,
    isDark: Boolean,
    onToggleBucket: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgePill(
                    text = "${idea.category} · ${idea.duration}",
                    backgroundColor = MintCalm.copy(alpha = 0.2f),
                    textColor = MintCalm
                )

                IconButton(onClick = onToggleBucket) {
                    Icon(
                        imageVector = if (idea.isSavedToBucketList) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save to bucket list",
                        tint = RosePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = idea.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = idea.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cost: ${idea.estimatedCost}",
                    fontSize = 11.sp,
                    color = RosePrimary,
                    fontWeight = FontWeight.SemiBold
                )

                if (idea.isSavedToBucketList) {
                    Text(
                        text = "Saved in Bucket List ❤️",
                        fontSize = 11.sp,
                        color = MintCalm,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
