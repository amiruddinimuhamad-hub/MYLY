package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.getStrings
import com.example.ui.components.BadgePill
import com.example.ui.components.GlassCard
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldenSun
import com.example.ui.theme.MintCalm
import com.example.ui.theme.PeachWarm
import com.example.ui.theme.RosePrimary
import com.example.ui.viewmodel.CoupleViewModel

data class DrawPathSegment(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidth: Float
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GamesScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val strings = getStrings(language)
    val coupleProfile by viewModel.coupleProfile.collectAsState()

    var selectedGameSection by remember { mutableStateOf(0) } // 0: Trivia, 1: TicTacToe, 2: Word Duel, 3: Doodle, 4: 20 Questions, 5: Async (This/That & Stats)

    val tabs = listOf(
        "Trivia" to "❓",
        "Tic-Tac-Toe" to "❤️",
        "Word Duel" to "🔤",
        "Doodle" to "🎨",
        "20 Questions" to "💭",
        "Async & Stats" to "📊"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = strings.gamesTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color(0xFF2D2530)
        )
        Text(
            text = "Play together in real-time or asynchronously across time zones 🎮",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Game Selector Pills
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, (title, emoji) ->
                val isSelected = selectedGameSection == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) RosePrimary else if (isDark) Color(0x33FFFFFF) else Color(0x20F39682))
                        .clickable { selectedGameSection = index }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = emoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedGameSection) {
            0 -> TriviaSection(viewModel, isDark)
            1 -> TicTacToeSection(viewModel, isDark)
            2 -> WordDuelSection(viewModel, isDark)
            3 -> DoodleSection(viewModel, isDark)
            4 -> DeepPromptsSection(viewModel, isDark)
            5 -> AsyncAndStatsSection(viewModel, isDark)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// 1. COUPLE TRIVIA
@Composable
private fun TriviaSection(viewModel: CoupleViewModel, isDark: Boolean) {
    val currentIndex by viewModel.currentTriviaIndex.collectAsState()
    val selectedOption by viewModel.selectedTriviaOption.collectAsState()
    val score by viewModel.triviaScore.collectAsState()
    val question = viewModel.triviaQuestions[currentIndex]

    GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgePill("Round ${currentIndex + 1} of ${viewModel.triviaQuestions.size}", backgroundColor = RosePrimary.copy(alpha = 0.15f), textColor = RosePrimary)
                Text(
                    text = "Score: $score ❤️",
                    fontWeight = FontWeight.Bold,
                    color = RosePrimary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            question.options.forEachIndexed { index, option ->
                val isAnswered = selectedOption != null
                val isSelected = selectedOption == index
                val isCorrect = index == question.correctIndex

                val btnBg = when {
                    !isAnswered -> if (isDark) Color(0x22FFFFFF) else Color(0x33F39682)
                    isCorrect -> MintCalm.copy(alpha = 0.35f)
                    isSelected -> Color(0x33E57373)
                    else -> if (isDark) Color(0x11FFFFFF) else Color(0x11F39682)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(btnBg)
                        .clickable(enabled = !isAnswered) {
                            viewModel.selectTriviaAnswer(index)
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected || (isAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isAnswered && isCorrect) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Correct", tint = MintCalm)
                        } else if (isAnswered && isSelected) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Incorrect", tint = Color(0xFFE57373))
                        }
                    }
                }
            }

            if (selectedOption != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(RosePrimary.copy(alpha = 0.1f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡 ${question.partnerFunFact}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.nextTriviaQuestion() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                ) {
                    Text("Next Trivia Round ✨")
                }
            }
        }
    }
}

// 2. TIC-TAC-TOE
@Composable
private fun TicTacToeSection(viewModel: CoupleViewModel, isDark: Boolean) {
    val state by viewModel.ticTacToeState.collectAsState()

    GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgePill(
                    text = if (state.isHeartTurn) "❤️ Turn" else "⭐ Turn",
                    backgroundColor = if (state.isHeartTurn) RosePrimary.copy(alpha = 0.2f) else GoldenSun.copy(alpha = 0.2f),
                    textColor = if (state.isHeartTurn) RosePrimary else GoldenSun
                )
                IconButton(onClick = { viewModel.resetTicTacToe() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Game", tint = RosePrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (state.isGameOver) {
                Text(
                    text = when (state.winner) {
                        "DRAW" -> "It's a tie! Both lovers are equally matched! 🤝"
                        "❤️" -> "❤️ Hearts win the round! Sweet kiss rewarded! 💋"
                        "⭐" -> "⭐ Stars win the round! Midnight wish granted! 🌟"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = RosePrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 3x3 Grid
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color(0x33000000) else Color(0x22F39682))
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (row in 0..2) {
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (col in 0..2) {
                                val index = row * 3 + col
                                val cellVal = state.board[index]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0x44FFFFFF) else Color.White)
                                        .clickable { viewModel.onTicTacToeCellTap(index) }
                                        .testTag("tictactoe_cell_$index"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = cellVal, fontSize = 28.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. WORD DUEL
@Composable
private fun WordDuelSection(viewModel: CoupleViewModel, isDark: Boolean) {
    val roundIndex by viewModel.currentWordRound.collectAsState()
    val wordInput by viewModel.wordInput.collectAsState()
    val wordResult by viewModel.wordResultCorrect.collectAsState()
    val currentRound = viewModel.wordRounds[roundIndex]

    GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            BadgePill("Word Duel · Round ${roundIndex + 1}", backgroundColor = MintCalm.copy(alpha = 0.2f), textColor = MintCalm)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Unscramble this couple secret word:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentRound.scrambled,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                color = RosePrimary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hint: ${currentRound.hint}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = wordInput,
                onValueChange = { viewModel.setWordInput(it) },
                label = { Text("Your answer") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("word_duel_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.submitWordDuel() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
            ) {
                Text("Submit Guess 🔤", fontWeight = FontWeight.Bold)
            }

            if (wordResult == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "🎉 Brilliant! Correct word unscrambled! +30 XP", color = MintCalm, fontWeight = FontWeight.Bold)
            } else if (wordResult == false) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Not quite yet! Check the hint and try again ❤️", color = Color(0xFFE57373), fontSize = 12.sp)
            }
        }
    }
}

// 4. SHARED DOODLE BOARD
@Composable
private fun DoodleSection(viewModel: CoupleViewModel, isDark: Boolean) {
    val paths = remember { mutableStateListOf<DrawPathSegment>() }
    var currentColor by remember { mutableStateOf(RosePrimary) }
    var currentStroke by remember { mutableStateOf(8f) }
    var doodleSentMsg by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgePill("Shared Doodle Canvas 🎨", backgroundColor = PeachWarm.copy(alpha = 0.2f), textColor = PeachWarm)
                Row {
                    IconButton(onClick = { paths.clear() }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear Canvas", tint = Color(0xFFE57373))
                    }
                    IconButton(onClick = {
                        viewModel.sendThinkingOfYou()
                        doodleSentMsg = true
                    }) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send Doodle", tint = RosePrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Palette picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val colors = listOf(RosePrimary, PeachWarm, MintCalm, GoldenSun, Color.White, Color.Black)
                colors.forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                width = if (currentColor == c) 2.5.dp else 1.dp,
                                color = if (currentColor == c) RosePrimary else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable { currentColor = c }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0xFF1E1A26) else Color(0xFFFFFDFC))
                    .border(1.dp, Color(0x33F39682), RoundedCornerShape(16.dp))
            ) {
                var previousOffset by remember { mutableStateOf<Offset?>(null) }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    previousOffset = offset
                                },
                                onDrag = { change, _ ->
                                    val current = change.position
                                    previousOffset?.let { prev ->
                                        paths.add(DrawPathSegment(prev, current, currentColor, currentStroke))
                                    }
                                    previousOffset = current
                                },
                                onDragEnd = {
                                    previousOffset = null
                                }
                            )
                        }
                ) {
                    paths.forEach { segment ->
                        drawLine(
                            color = segment.color,
                            start = segment.start,
                            end = segment.end,
                            strokeWidth = segment.strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }

                if (paths.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Draw a sweet doodle for your partner here ✨",
                            fontSize = 12.sp,
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            if (doodleSentMsg) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Doodle sent to partner's screen with gentle vibration! 💌",
                    fontSize = 11.sp,
                    color = MintCalm,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// 5. 20 QUESTIONS / DEEP PROMPTS
@Composable
private fun DeepPromptsSection(viewModel: CoupleViewModel, isDark: Boolean) {
    val promptIndex by viewModel.currentPromptIndex.collectAsState()
    val prompt = viewModel.deepPrompts[promptIndex]

    GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            BadgePill("Deep Call Prompts 💭", backgroundColor = Color(0x20E3D5F5), textColor = Color(0xFF8E44AD))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Question ${promptIndex + 1} of ${viewModel.deepPrompts.size}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "\"$prompt\"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { viewModel.nextDeepPrompt() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
            ) {
                Text("Next Deep Prompt 🔮")
            }
        }
    }
}

// 6. ASYNC GAMES & STATS
@Composable
private fun AsyncAndStatsSection(viewModel: CoupleViewModel, isDark: Boolean) {
    val thisOrThatIndex by viewModel.thisOrThatIndex.collectAsState()
    val myChoice by viewModel.myThisOrThatChoice.collectAsState()
    val thisOrThat = viewModel.thisOrThatList[thisOrThatIndex]
    val scores by viewModel.gameScores.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        // This or That Card
        GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
            Column(modifier = Modifier.fillMaxWidth()) {
                BadgePill("Daily Async 'This or That' ⚖️", backgroundColor = FlameOrange.copy(alpha = 0.2f), textColor = FlameOrange)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Pick your preference to compare with partner's pick:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Option A
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (myChoice == 0) RosePrimary else if (isDark) Color(0x33FFFFFF) else Color(0x22F39682))
                            .clickable { viewModel.chooseThisOrThat(0) }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = thisOrThat.optionA,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (myChoice == 0) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Option B
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (myChoice == 1) RosePrimary else if (isDark) Color(0x33FFFFFF) else Color(0x22F39682))
                            .clickable { viewModel.chooseThisOrThat(1) }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = thisOrThat.optionB,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (myChoice == 1) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (thisOrThat.partnerAChoice != null && thisOrThat.partnerBChoice != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Match result: ${if (thisOrThat.partnerAChoice == thisOrThat.partnerBChoice) "100% In-Sync! Soulmates! 💖" else "Opposites attract! Cute balance! ☯️"}",
                        fontSize = 11.sp,
                        color = MintCalm,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Game Stats Card
        GlassCard(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = GoldenSun)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Game Night Trophy Room",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                scores.forEach { score ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = score.gameTitle, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text(
                            text = "${score.gamesPlayed} played · Alex ${score.partnerAWins}W / Sam ${score.partnerBWins}W",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
