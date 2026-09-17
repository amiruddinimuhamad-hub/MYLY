package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CoupleProfile
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.RosePrimary

@Composable
fun MascotHeader(
    profile: CoupleProfile,
    onToggleActiveUser: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        isDark = isDark,
        elevation = 3.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Mascot image & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_couple_mascot),
                        contentDescription = "MYLY Couple Mascot",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, RosePrimary.copy(alpha = 0.5f), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MYLY",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) Color.White else Color(0xFF2D2530),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "✨", fontSize = 16.sp)
                        }
                        Text(
                            text = "${profile.distanceKm} km apart · ${profile.daysTogether} days together",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) Color(0xFFB5ADC2) else Color(0xFF756A7A)
                        )
                    }
                }

                // Level badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(RosePrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Lv. ${profile.currentLevel}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = RosePrimary
                        )
                        Text(
                            text = profile.levelTitle,
                            fontSize = 10.sp,
                            color = if (isDark) Color(0xFFB5ADC2) else Color(0xFF756A7A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Partner connection row with dual timezone & active user switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x66FFF0EC))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Partner A
                val isAActive = profile.currentActiveUser == profile.partnerAName
                Column(horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.partnerAName,
                            fontWeight = if (isAActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (isAActive) RosePrimary else MaterialTheme.colorScheme.onSurface
                        )
                        if (isAActive) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(RosePrimary)
                            )
                        }
                    }
                    Text(
                        text = "${profile.partnerACity} · 23:45 🌙",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Switch button
                IconButton(
                    onClick = onToggleActiveUser,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RosePrimary.copy(alpha = 0.2f))
                        .testTag("switch_partner_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch active partner view",
                        tint = RosePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Partner B
                val isBActive = profile.currentActiveUser == profile.partnerBName
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isBActive) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(RosePrimary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = profile.partnerBName,
                            fontWeight = if (isBActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (isBActive) RosePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${profile.partnerBCity} · 14:45 ☀️",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
