package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CyclePhase
import com.example.data.model.CycleSharingMode
import com.example.data.model.getStrings
import com.example.ui.components.BadgePill
import com.example.ui.components.GlassCard
import com.example.ui.theme.MintCalm
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.RosePrimary
import com.example.ui.viewmodel.CoupleViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CycleScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val cycleStatus by viewModel.cycleStatus.collectAsState()
    val isCycleUnlocked by viewModel.isCycleUnlocked.collectAsState()
    val showConsentDialog by viewModel.showSharingConsentDialog.collectAsState()
    val strings = getStrings(language)

    var pendingSharingMode by remember { mutableStateOf(cycleStatus.sharingMode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = strings.cycleCareTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF2D2530)
                )
                Text(
                    text = "Gentle wellness, cycle predictions & partner support 🌱",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Biometric lock status button
            IconButton(
                onClick = {
                    if (isCycleUnlocked) viewModel.lockCycleView() else viewModel.unlockCycleView()
                }
            ) {
                Icon(
                    imageVector = if (isCycleUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = "Lock/Unlock Health Privacy",
                    tint = if (isCycleUnlocked) MintCalm else RosePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Biometric Lock Speed Bump if locked
        if (!isCycleUnlocked) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(RosePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric Lock",
                            tint = RosePrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Private Health Vault",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cycle and health predictions are shielded behind biometric / PIN encryption so family and friends nearby cannot see.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { viewModel.unlockCycleView() },
                        colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("unlock_cycle_button")
                    ) {
                        Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify Biometric / PIN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // UNLOCKED VIEW: Full cycle dashboard
            // 1. Current Phase Card
            GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BadgePill(
                            text = "${cycleStatus.currentPhase.icon} ${cycleStatus.currentPhase.displayName}",
                            backgroundColor = Color(cycleStatus.currentPhase.colorHex).copy(alpha = 0.2f),
                            textColor = Color(cycleStatus.currentPhase.colorHex)
                        )
                        Text(
                            text = "Day ${cycleStatus.dayOfCycle} of ${cycleStatus.totalCycleLength}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { cycleStatus.dayOfCycle.toFloat() / cycleStatus.totalCycleLength.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(cycleStatus.currentPhase.colorHex),
                        trackColor = Color(cycleStatus.currentPhase.colorHex).copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fertile Window: Days ${cycleStatus.fertileWindowStartDay}-${cycleStatus.fertileWindowEndDay} ✨",
                            fontSize = 12.sp,
                            color = MintCalm,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Regular Cycle",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Gentle Partner Nudge (Supportive, Never Clinical)
            GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = "Partner Care",
                            tint = RosePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.partnerSupportTip,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = RosePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = cycleStatus.partnerNudge,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Symptoms & Mood Notes
            GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Symptom & Mood Notes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val symptomsList = listOf("✨ Energetic", "🍵 Craving Warm Tea", "🎨 Creative Mind", "🥰 Extra Affectionate", "😌 Peaceful", "🍫 Sweet Tooth")
                        symptomsList.forEach { s ->
                            BadgePill(
                                text = s,
                                backgroundColor = if (cycleStatus.symptoms.any { s.contains(it) }) RosePrimary.copy(alpha = 0.2f) else Color(0x15FFFFFF),
                                textColor = if (cycleStatus.symptoms.any { s.contains(it) }) RosePrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Consent Speed Bump & Partner Sharing Controls
            GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = "Privacy Shield", tint = MintCalm)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.sharingPrivacy,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        BadgePill(
                            text = when (cycleStatus.sharingMode) {
                                CycleSharingMode.PRIVATE -> "Private 🔒"
                                CycleSharingMode.PHASE_ONLY -> "Phase Only 🌤️"
                                CycleSharingMode.FULL_DETAIL -> "Full Detail 💖"
                            },
                            backgroundColor = MintCalm.copy(alpha = 0.15f),
                            textColor = MintCalm
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "You are in 100% control of what your partner sees. Adjust or revoke at any second with a plain-language confirmation.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.openSharingConsentDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Configure Partner Sharing Level ⚙️")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Phase Switcher for Logging
            GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Log Today's Cycle Phase",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CyclePhase.values().forEach { phase ->
                            val isCurrent = cycleStatus.currentPhase == phase
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isCurrent) Color(phase.colorHex) else Color(0x15888888))
                                    .clickable { viewModel.updateCyclePhase(phase, if (phase == CyclePhase.MENSTRUAL) 2 else 10) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = phase.icon, fontSize = 16.sp)
                                    Text(
                                        text = phase.name.take(4),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Consent Speed Bump Dialog
    if (showConsentDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSharingConsentDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = RosePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Partner Sharing Consent", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Plain-Language Consent Speed Bump:\nYour cycle and health data belongs solely to you. Please select what you want your partner to see in their app:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pendingSharingMode = CycleSharingMode.PRIVATE }
                    ) {
                        RadioButton(
                            selected = pendingSharingMode == CycleSharingMode.PRIVATE,
                            onClick = { pendingSharingMode = CycleSharingMode.PRIVATE }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("🔒 Private (Nothing Shared)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Partner cannot see any cycle predictions or notes.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pendingSharingMode = CycleSharingMode.PHASE_ONLY }
                    ) {
                        RadioButton(
                            selected = pendingSharingMode == CycleSharingMode.PHASE_ONLY,
                            onClick = { pendingSharingMode = CycleSharingMode.PHASE_ONLY }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("🌤️ Phase Only (Gentle Tips)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Shares general phase name & supportive tips only.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pendingSharingMode = CycleSharingMode.FULL_DETAIL }
                    ) {
                        RadioButton(
                            selected = pendingSharingMode == CycleSharingMode.FULL_DETAIL,
                            onClick = { pendingSharingMode = CycleSharingMode.FULL_DETAIL }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("💖 Full Detail", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Shares phase, day countdown, and comfort notes.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.setSharingMode(pendingSharingMode) },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                ) {
                    Text("Confirm Setting")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissSharingConsentDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
