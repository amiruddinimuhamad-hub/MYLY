package com.example.ui.viewmodel

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DateIdeaEntity
import com.example.data.local.GameScoreEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.PlaylistSongEntity
import com.example.data.model.AppLanguage
import com.example.data.model.CoupleProfile
import com.example.data.model.CyclePhase
import com.example.data.model.CycleSharingMode
import com.example.data.model.CycleStatus
import com.example.data.model.DailyQuote
import com.example.data.model.QuoteMood
import com.example.data.model.StreakInfo
import com.example.data.model.ThisOrThatOption
import com.example.data.model.TriviaQuestion
import com.example.data.model.WordDuelRound
import com.example.data.model.getStrings
import com.example.data.repository.CoupleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val iconName: String) {
    TOGETHER("Together"),
    GAMES("Games"),
    CYCLE("Cycle Care"),
    DATES("Date Ideas"),
    OUR_WORLD("Our World")
}

data class TicTacToeState(
    val board: List<String> = List(9) { "" }, // "", "❤️", "⭐"
    val isHeartTurn: Boolean = true,
    val winner: String? = null, // null, "❤️", "⭐", "DRAW"
    val isGameOver: Boolean = false
)

class CoupleViewModel(
    private val repository: CoupleRepository,
    private val appContext: Context
) : ViewModel() {

    // Active bottom navigation tab
    private val _currentTab = MutableStateFlow(MainTab.TOGETHER)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Floating heart triggers for "Thinking of you"
    private val _heartPulseEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 5)
    val heartPulseEvent: SharedFlow<Unit> = _heartPulseEvent.asSharedFlow()

    // Toast message trigger
    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    // Repository Flows
    val coupleProfile = repository.coupleProfile
    val streakInfo = repository.streakInfo
    val cycleStatus = repository.cycleStatus
    val currentQuote = repository.currentQuote
    val currentLanguage = repository.currentLanguage
    val isDarkTheme = repository.isDarkTheme
    val isFamilyGlanceShield = repository.isFamilyGlanceShield
    val savingsSaved = repository.savingsSaved
    val savingsGoal = repository.savingsGoal

    val allDateIdeas: StateFlow<List<DateIdeaEntity>> = repository.allDateIdeas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bucketListIdeas: StateFlow<List<DateIdeaEntity>> = repository.bucketListIdeas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSongs: StateFlow<List<PlaylistSongEntity>> = repository.allSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMemories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gameScores: StateFlow<List<GameScoreEntity>> = repository.allGameScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Generation state
    private val _isGeneratingIdeas = MutableStateFlow(false)
    val isGeneratingIdeas: StateFlow<Boolean> = _isGeneratingIdeas.asStateFlow()

    // Biometric/PIN unlocked state for Cycle Care
    private val _isCycleUnlocked = MutableStateFlow(false)
    val isCycleUnlocked: StateFlow<Boolean> = _isCycleUnlocked.asStateFlow()

    // Consent Speed Bump Dialog visibility
    private val _showSharingConsentDialog = MutableStateFlow(false)
    val showSharingConsentDialog: StateFlow<Boolean> = _showSharingConsentDialog.asStateFlow()

    // Add Memory Dialog visibility
    private val _showAddMemoryDialog = MutableStateFlow(false)
    val showAddMemoryDialog: StateFlow<Boolean> = _showAddMemoryDialog.asStateFlow()

    // Add Song Dialog visibility
    private val _showAddSongDialog = MutableStateFlow(false)
    val showAddSongDialog: StateFlow<Boolean> = _showAddSongDialog.asStateFlow()

    // --- GAME STATES ---
    // Trivia
    val triviaQuestions = listOf(
        TriviaQuestion(
            question = "What was our very first inside joke on our late-night call?",
            options = listOf("The upside-down pizza slice", "The runaway cat on Zoom", "The 3 AM sleepy accent", "The mismatched socks flight"),
            correctIndex = 2,
            partnerFunFact = "You both laughed until 4:30 AM before falling asleep on speaker!"
        ),
        TriviaQuestion(
            question = "Where is our dream in-person reunion destination?",
            options = listOf("A snowy onsen town in Hokkaido", "A quiet cottage in the Scottish Highlands", "A vibrant night market in Penang", "A balcony flat in Amalfi"),
            correctIndex = 0,
            partnerFunFact = "Saved on your joint bucket list since day 40!"
        ),
        TriviaQuestion(
            question = "What comfort snack always cheers your partner up on a tough day?",
            options = listOf("Matcha latte with boba", "Crispy hot churros", "Salted caramel dark chocolate", "Warm cinnamon pastry"),
            correctIndex = 2,
            partnerFunFact = "Always paired with a warm audio note!"
        )
    )
    private val _currentTriviaIndex = MutableStateFlow(0)
    val currentTriviaIndex: StateFlow<Int> = _currentTriviaIndex.asStateFlow()

    private val _selectedTriviaOption = MutableStateFlow<Int?>(null)
    val selectedTriviaOption: StateFlow<Int?> = _selectedTriviaOption.asStateFlow()

    private val _triviaScore = MutableStateFlow(0)
    val triviaScore: StateFlow<Int> = _triviaScore.asStateFlow()

    // Word Duel
    val wordRounds = listOf(
        WordDuelRound("ESMAMSNOO", "SAMEMOON", "What we look at together every night 🌙"),
        WordDuelRound("NEUINRO", "REUNION", "What we count the days down to ✈️"),
        WordDuelRound("EPELTMEVI", "TELEPATHY", "When we text the exact same word simultaneously 💭"),
        WordDuelRound("TPAREHBAT", "HEARTBEAT", "The gentle tap sent across the miles 💕")
    )
    private val _currentWordRound = MutableStateFlow(0)
    val currentWordRound: StateFlow<Int> = _currentWordRound.asStateFlow()

    private val _wordInput = MutableStateFlow("")
    val wordInput: StateFlow<String> = _wordInput.asStateFlow()

    private val _wordResultCorrect = MutableStateFlow<Boolean?>(null)
    val wordResultCorrect: StateFlow<Boolean?> = _wordResultCorrect.asStateFlow()

    // Tic-Tac-Toe
    private val _ticTacToeState = MutableStateFlow(TicTacToeState())
    val ticTacToeState: StateFlow<TicTacToeState> = _ticTacToeState.asStateFlow()

    // 20 Questions / Deep Prompts
    val deepPrompts = listOf(
        "What was the exact moment you realized this distance was 1000% worth every single mile?",
        "If you could teleport to my room for just 15 minutes right now, what would we do first?",
        "What is a small habit of mine on video calls that never fails to make you smile?",
        "When our reunion flight lands, describe the first 60 seconds of our hug in airport arrivals.",
        "What song lyric currently describes how much I mean to you across these time zones?"
    )
    private val _currentPromptIndex = MutableStateFlow(0)
    val currentPromptIndex: StateFlow<Int> = _currentPromptIndex.asStateFlow()

    // This or That
    val thisOrThatList = listOf(
        ThisOrThatOption("Morning coffee video call ☕", "Midnight pillow-talk call 🌙", partnerAChoice = 1, partnerBChoice = 1),
        ThisOrThatOption("Cooking the same meal together 🍝", "Ordering surprise food delivery for each other 🍱", partnerAChoice = 0, partnerBChoice = 1),
        ThisOrThatOption("Handwritten letters by post 💌", "Voice note journal every night 🎙️", partnerAChoice = 0, partnerBChoice = 0),
        ThisOrThatOption("Snowy cabin weekend retreat ❄️", "Tropical beach sunset walk 🌴", partnerAChoice = 0, partnerBChoice = null)
    )
    private val _thisOrThatIndex = MutableStateFlow(0)
    val thisOrThatIndex: StateFlow<Int> = _thisOrThatIndex.asStateFlow()
    private val _myThisOrThatChoice = MutableStateFlow<Int?>(null)
    val myThisOrThatChoice: StateFlow<Int?> = _myThisOrThatChoice.asStateFlow()

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    // "Thinking of You" Instant Heartbeat Interaction
    fun sendThinkingOfYou() {
        viewModelScope.launch {
            _heartPulseEvent.emit(Unit)
            triggerHapticPulse()
            repository.addCoupleXp(15)
            val strings = getStrings(currentLanguage.value)
            _snackMessage.value = strings.thinkingOfYouSent
            delay(3000)
            _snackMessage.value = null
        }
    }

    private fun triggerHapticPulse() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(120)
                }
            }
        } catch (e: Exception) {
            // Ignore if vibration unavailable
        }
    }

    fun checkInStreak() {
        repository.checkInCurrentPartner()
        triggerHapticPulse()
        _snackMessage.value = "Streak renewed! Love is growing stronger ❤️🔥"
        viewModelScope.launch {
            delay(2500)
            _snackMessage.value = null
        }
    }

    fun activateStreakFreeze() {
        repository.activateStreakFreeze()
        _snackMessage.value = "Weekly Freeze applied! Your streak is safely shielded ❄️"
        viewModelScope.launch {
            delay(2500)
            _snackMessage.value = null
        }
    }

    fun togglePartner() {
        repository.toggleActiveUser()
        val user = repository.coupleProfile.value.currentActiveUser
        _snackMessage.value = "Switched active view to $user"
        viewModelScope.launch {
            delay(2000)
            _snackMessage.value = null
        }
    }

    fun changeQuoteMood(mood: QuoteMood) {
        viewModelScope.launch {
            repository.refreshQuote(mood)
        }
    }

    fun generateDateIdeas(interests: String, budget: String, isVirtual: Boolean) {
        viewModelScope.launch {
            _isGeneratingIdeas.value = true
            try {
                repository.generateCustomDateIdeas(interests, budget, isVirtual)
                _snackMessage.value = "Fresh tailored date ideas crafted! ✨"
            } catch (e: Exception) {
                _snackMessage.value = "Date ideas updated!"
            } finally {
                _isGeneratingIdeas.value = false
                delay(2500)
                _snackMessage.value = null
            }
        }
    }

    fun surpriseMeDate() {
        val surprises = listOf(
            Triple("Stargazing & Indie Music", "Free", true),
            Triple("Gourmet Breakfast Delivery", "$$", true),
            Triple("Rooftop Sunset Drinks", "$$", false),
            Triple("Escape Room Online Duel", "$", true)
        )
        val picked = surprises.random()
        generateDateIdeas(picked.first, picked.second, picked.third)
    }

    fun toggleBucketList(idea: DateIdeaEntity) {
        viewModelScope.launch {
            repository.toggleBucketList(idea.id, !idea.isSavedToBucketList)
            val strings = getStrings(currentLanguage.value)
            if (!idea.isSavedToBucketList) {
                _snackMessage.value = strings.bucketListSaved
            } else {
                _snackMessage.value = "Removed from bucket list"
            }
            delay(2000)
            _snackMessage.value = null
        }
    }

    fun addSavingsContribution(amount: Int) {
        repository.addSavings(amount)
        _snackMessage.value = "Added $$amount to Next Visit fund! Closer to reunion ✈️"
        viewModelScope.launch {
            delay(2000)
            _snackMessage.value = null
        }
    }

    // Biometric / PIN Lock handling
    fun unlockCycleView() {
        _isCycleUnlocked.value = true
        _snackMessage.value = "Biometric / PIN verified: Cycle health unlocked 🔒"
        viewModelScope.launch {
            delay(2000)
            _snackMessage.value = null
        }
    }

    fun lockCycleView() {
        _isCycleUnlocked.value = false
    }

    fun openSharingConsentDialog() {
        _showSharingConsentDialog.value = true
    }

    fun dismissSharingConsentDialog() {
        _showSharingConsentDialog.value = false
    }

    fun setSharingMode(mode: CycleSharingMode) {
        repository.updateCycleSharingMode(mode)
        _showSharingConsentDialog.value = false
        _snackMessage.value = "Partner sharing updated to: ${mode.name.replace('_', ' ')}"
        viewModelScope.launch {
            delay(2000)
            _snackMessage.value = null
        }
    }

    fun updateCyclePhase(phase: CyclePhase, day: Int) {
        repository.updateCyclePhase(phase, day)
    }

    // Memory & Playlist dialogs
    fun openAddMemoryDialog() { _showAddMemoryDialog.value = true }
    fun closeAddMemoryDialog() { _showAddMemoryDialog.value = false }

    fun openAddSongDialog() { _showAddSongDialog.value = true }
    fun closeAddSongDialog() { _showAddSongDialog.value = false }

    fun addNewMemory(title: String, note: String, location: String) {
        viewModelScope.launch {
            repository.addMemory(
                MemoryEntity(
                    title = title,
                    note = note,
                    dateString = "Just now",
                    locationName = location.ifBlank { "Tokyo & London" },
                    heartCount = 1
                )
            )
            _showAddMemoryDialog.value = false
            _snackMessage.value = "Added to your shared memory journal 📸"
            delay(2000)
            _snackMessage.value = null
        }
    }

    fun heartMemory(id: Int) {
        viewModelScope.launch {
            repository.addMemoryHeart(id)
            triggerHapticPulse()
        }
    }

    fun addNewSong(title: String, artist: String, why: String, platform: String) {
        viewModelScope.launch {
            val user = repository.coupleProfile.value.currentActiveUser
            repository.addSong(
                PlaylistSongEntity(
                    title = title,
                    artist = artist,
                    dedicatedBy = user,
                    whyThisSong = why,
                    platform = platform,
                    externalUrl = "https://open.spotify.com",
                    addedDate = "Today"
                )
            )
            _showAddSongDialog.value = false
            _snackMessage.value = "Added '$title' to Our Playlist 🎶"
            delay(2000)
            _snackMessage.value = null
        }
    }

    // --- GAMES LOGIC ---
    // Trivia
    fun selectTriviaAnswer(index: Int) {
        if (_selectedTriviaOption.value != null) return
        _selectedTriviaOption.value = index
        val currentQ = triviaQuestions[_currentTriviaIndex.value]
        if (index == currentQ.correctIndex) {
            _triviaScore.value += 1
            repository.addCoupleXp(20)
            triggerHapticPulse()
        }
    }

    fun nextTriviaQuestion() {
        _selectedTriviaOption.value = null
        if (_currentTriviaIndex.value < triviaQuestions.size - 1) {
            _currentTriviaIndex.value += 1
        } else {
            _currentTriviaIndex.value = 0
            viewModelScope.launch {
                repository.recordGameWin("trivia", true)
            }
        }
    }

    // Word Duel
    fun setWordInput(input: String) {
        _wordInput.value = input
    }

    fun submitWordDuel() {
        val target = wordRounds[_currentWordRound.value].targetWord
        if (_wordInput.value.trim().equals(target, ignoreCase = true)) {
            _wordResultCorrect.value = true
            triggerHapticPulse()
            repository.addCoupleXp(30)
            viewModelScope.launch {
                repository.recordGameWin("word_duel", true)
                delay(1500)
                _wordInput.value = ""
                _wordResultCorrect.value = null
                _currentWordRound.value = (_currentWordRound.value + 1) % wordRounds.size
            }
        } else {
            _wordResultCorrect.value = false
            viewModelScope.launch {
                delay(1200)
                _wordResultCorrect.value = null
            }
        }
    }

    // Tic-Tac-Toe
    fun onTicTacToeCellTap(index: Int) {
        val current = _ticTacToeState.value
        if (current.isGameOver || current.board[index].isNotEmpty()) return

        val newBoard = current.board.toMutableList()
        val symbol = if (current.isHeartTurn) "❤️" else "⭐"
        newBoard[index] = symbol
        val winner = checkTicTacToeWinner(newBoard)
        val isFull = newBoard.all { it.isNotEmpty() }
        val isOver = winner != null || isFull

        _ticTacToeState.value = current.copy(
            board = newBoard,
            isHeartTurn = !current.isHeartTurn,
            winner = winner ?: if (isFull) "DRAW" else null,
            isGameOver = isOver
        )

        triggerHapticPulse()

        if (isOver) {
            viewModelScope.launch {
                if (winner != null && winner != "DRAW") {
                    repository.recordGameWin("tic_tac_toe", winner == "❤️")
                }
            }
        }
    }

    fun resetTicTacToe() {
        _ticTacToeState.value = TicTacToeState()
    }

    private fun checkTicTacToeWinner(b: List<String>): String? {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // cols
            listOf(0, 4, 8), listOf(2, 4, 6)             // diagonals
        )
        for (line in lines) {
            val (x, y, z) = line
            if (b[x].isNotEmpty() && b[x] == b[y] && b[y] == b[z]) {
                return b[x]
            }
        }
        return null
    }

    // 20 Questions
    fun nextDeepPrompt() {
        _currentPromptIndex.value = (_currentPromptIndex.value + 1) % deepPrompts.size
        triggerHapticPulse()
        repository.addCoupleXp(10)
    }

    // This or That
    fun chooseThisOrThat(choice: Int) {
        _myThisOrThatChoice.value = choice
        triggerHapticPulse()
        repository.addCoupleXp(15)
        viewModelScope.launch {
            delay(1000)
            _thisOrThatIndex.value = (_thisOrThatIndex.value + 1) % thisOrThatList.size
            _myThisOrThatChoice.value = null
        }
    }

    // Settings
    fun setLanguage(language: AppLanguage) {
        repository.setLanguage(language)
    }

    fun setDarkTheme(enabled: Boolean) {
        repository.setDarkTheme(enabled)
    }

    fun toggleFamilyGlanceShield() {
        val current = isFamilyGlanceShield.value
        repository.setFamilyGlanceShield(!current)
        val strings = getStrings(currentLanguage.value)
        _snackMessage.value = if (!current) strings.familyGlanceOn else strings.familyGlanceOff
        viewModelScope.launch {
            delay(2000)
            _snackMessage.value = null
        }
    }

    fun clearSnackMessage() {
        _snackMessage.value = null
    }
}

class CoupleViewModelFactory(
    private val repository: CoupleRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoupleViewModel::class.java)) {
            return CoupleViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
