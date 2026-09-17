package com.example.data.repository

import com.example.ai.GeminiDateService
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CoupleRepository(
    private val database: AppDatabase,
    private val geminiService: GeminiDateService = GeminiDateService()
) {
    private val dateIdeaDao = database.dateIdeaDao()
    private val playlistDao = database.playlistSongDao()
    private val memoryDao = database.memoryDao()
    private val gameScoreDao = database.gameScoreDao()

    // Persistent & reactive flows
    val allDateIdeas: Flow<List<DateIdeaEntity>> = dateIdeaDao.getAllDateIdeas()
    val bucketListIdeas: Flow<List<DateIdeaEntity>> = dateIdeaDao.getBucketListIdeas()
    val allSongs: Flow<List<PlaylistSongEntity>> = playlistDao.getAllSongs()
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()
    val allGameScores: Flow<List<GameScoreEntity>> = gameScoreDao.getAllScores()

    // Couple State
    private val _coupleProfile = MutableStateFlow(CoupleProfile())
    val coupleProfile: StateFlow<CoupleProfile> = _coupleProfile.asStateFlow()

    private val _streakInfo = MutableStateFlow(StreakInfo())
    val streakInfo: StateFlow<StreakInfo> = _streakInfo.asStateFlow()

    private val _cycleStatus = MutableStateFlow(CycleStatus())
    val cycleStatus: StateFlow<CycleStatus> = _cycleStatus.asStateFlow()

    private val _currentQuote = MutableStateFlow(
        DailyQuote(
            id = "1",
            text = "Day 214 apart, and every single second is proof that love doesn't measure distance in miles.",
            authorOrContext = "Personalized for Alex & Sam",
            mood = QuoteMood.SWEET,
            dayNumber = 214
        )
    )
    val currentQuote: StateFlow<DailyQuote> = _currentQuote.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isFamilyGlanceShield = MutableStateFlow(false)
    val isFamilyGlanceShield: StateFlow<Boolean> = _isFamilyGlanceShield.asStateFlow()

    // Savings fund for next visit
    private val _savingsSaved = MutableStateFlow(860)
    val savingsSaved: StateFlow<Int> = _savingsSaved.asStateFlow()

    private val _savingsGoal = MutableStateFlow(1200)
    val savingsGoal: StateFlow<Int> = _savingsGoal.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        // Seed initial date ideas
        val initialIdeas = listOf(
            DateIdeaEntity(
                title = "🍝 Synced Pasta & Candlelight Call",
                description = "Both order or cook the same simple creamy fettuccine recipe, set warm candlelight by your phones, and dine together over FaceTime.",
                category = "Virtual Cooking",
                estimatedCost = "$",
                duration = "1.5 hours",
                isSavedToBucketList = true
            ),
            DateIdeaEntity(
                title = "🌙 Same Sky Moon Gaze & Voice Memo",
                description = "Step outside at the exact same moment, gaze at the glowing moon, and trade intimate 2-minute audio notes describing the night breeze.",
                category = "Mindful Connection",
                estimatedCost = "Free",
                duration = "30 mins",
                isSavedToBucketList = true
            ),
            DateIdeaEntity(
                title = "🎨 Mystery Drawing & Soundtrack Stream",
                description = "Put on a synced ambient lofi playlist on Spotify, hop on video, and draw funny caricatures of each other without showing until the countdown ends!",
                category = "Creative Fun",
                estimatedCost = "Free",
                duration = "1 hour",
                isSavedToBucketList = false
            ),
            DateIdeaEntity(
                title = "🍿 Teleparty Film Night & Popcorn Swap",
                description = "Sync an animated Ghibli film using watch-together mode, with matching cinema candy packages mailed in advance.",
                category = "Virtual Cinema",
                estimatedCost = "Free",
                duration = "2 hours",
                isSavedToBucketList = true
            )
        )
        dateIdeaDao.insertAll(initialIdeas)

        // Seed initial songs
        val initialSongs = listOf(
            PlaylistSongEntity(
                title = "Until I Found You",
                artist = "Stephen Sanchez",
                dedicatedBy = "Alex",
                whyThisSong = "Played at the departure gate when I had to board the flight to Tokyo.",
                platform = "Spotify",
                externalUrl = "https://open.spotify.com",
                addedDate = "Yesterday"
            ),
            PlaylistSongEntity(
                title = "Here With Me",
                artist = "d4vd",
                dedicatedBy = "Sam",
                whyThisSong = "Our go-to song when falling asleep together on nighttime voice call.",
                platform = "Apple Music",
                externalUrl = "https://music.apple.com",
                addedDate = "3 days ago"
            ),
            PlaylistSongEntity(
                title = "Lover",
                artist = "Taylor Swift",
                dedicatedBy = "Alex",
                whyThisSong = "Can I go where you go? Can we always be this close?",
                platform = "Spotify",
                externalUrl = "https://open.spotify.com",
                addedDate = "Last week"
            )
        )
        playlistDao.insertAll(initialSongs)

        // Seed initial memories
        val initialMemories = listOf(
            MemoryEntity(
                title = "Airport Goodbye Hug in Terminal 3",
                note = "Held each other for 20 minutes before security. Promised each other: distance is just a test to see how far love can travel.",
                dateString = "July 14, 2026",
                locationName = "Tokyo Haneda Airport",
                heartCount = 18
            ),
            MemoryEntity(
                title = "Our First Synced Moon Gaze",
                note = "Both stepped onto our balconies under a full moon. 9,560 km apart, but looking at the exact same light.",
                dateString = "August 28, 2026",
                locationName = "Tokyo & London",
                heartCount = 24
            ),
            MemoryEntity(
                title = "Surprise Letter Delivery Day",
                note = "Received your handwritten parcel with the pressed cherry blossom leaf and instant coffee packets!",
                dateString = "September 02, 2026",
                locationName = "London Flat",
                heartCount = 31
            )
        )
        memoryDao.insertAll(initialMemories)

        // Seed initial game scores
        val initialScores = listOf(
            GameScoreEntity(gameKey = "trivia", gameTitle = "Couple Trivia", gamesPlayed = 12, partnerAWins = 6, partnerBWins = 6),
            GameScoreEntity(gameKey = "word_duel", gameTitle = "Word Duel", gamesPlayed = 8, partnerAWins = 5, partnerBWins = 3),
            GameScoreEntity(gameKey = "tic_tac_toe", gameTitle = "Love Tic-Tac-Toe", gamesPlayed = 15, partnerAWins = 7, partnerBWins = 8),
            GameScoreEntity(gameKey = "doodle", gameTitle = "Doodle Guesser", gamesPlayed = 10, partnerAWins = 5, partnerBWins = 5)
        )
        gameScoreDao.insertAll(initialScores)
    }

    // Actions
    fun checkInCurrentPartner() {
        val current = _streakInfo.value
        val isA = _coupleProfile.value.currentActiveUser == _coupleProfile.value.partnerAName
        val newA = if (isA) true else current.partnerACheckedIn
        val newB = if (!isA) true else current.partnerBCheckedIn
        val bothChecked = newA && newB
        val newStreak = if (bothChecked && !(current.partnerACheckedIn && current.partnerBCheckedIn)) {
            current.currentStreak + 1
        } else {
            current.currentStreak
        }

        _streakInfo.value = current.copy(
            partnerACheckedIn = newA,
            partnerBCheckedIn = newB,
            currentStreak = newStreak
        )

        // Add couple XP
        addCoupleXp(30)
    }

    fun activateStreakFreeze() {
        val current = _streakInfo.value
        if (current.hasFreezeAvailable && !current.isFreezeActive) {
            _streakInfo.value = current.copy(
                hasFreezeAvailable = false,
                isFreezeActive = true
            )
        }
    }

    fun toggleActiveUser() {
        val current = _coupleProfile.value
        val nextUser = if (current.currentActiveUser == current.partnerAName) current.partnerBName else current.partnerAName
        _coupleProfile.value = current.copy(currentActiveUser = nextUser)
    }

    fun addCoupleXp(points: Int) {
        val current = _coupleProfile.value
        val newXp = current.currentXp + points
        if (newXp >= current.maxXp) {
            _coupleProfile.value = current.copy(
                currentLevel = current.currentLevel + 1,
                currentXp = newXp - current.maxXp,
                levelTitle = when (current.currentLevel + 1) {
                    9 -> "Infinite Horizon"
                    10 -> "Eternal Orbit"
                    else -> "Galactic Soulmates"
                }
            )
        } else {
            _coupleProfile.value = current.copy(currentXp = newXp)
        }
    }

    suspend fun refreshQuote(mood: QuoteMood) {
        val profile = _coupleProfile.value
        val quoteText = geminiService.generatePersonalizedQuote(
            partnerA = profile.partnerAName,
            partnerB = profile.partnerBName,
            daysApart = profile.daysTogether,
            mood = mood.label
        )
        _currentQuote.value = DailyQuote(
            id = System.currentTimeMillis().toString(),
            text = quoteText,
            authorOrContext = "${mood.label} • Day ${profile.daysTogether} apart",
            mood = mood,
            dayNumber = profile.daysTogether
        )
    }

    suspend fun generateCustomDateIdeas(interests: String, budget: String, isVirtual: Boolean): List<DateIdeaEntity> {
        val ideas = geminiService.generateDateIdeas(interests, budget, isVirtual)
        dateIdeaDao.insertAll(ideas)
        addCoupleXp(50)
        return ideas
    }

    suspend fun toggleBucketList(ideaId: Int, isSaved: Boolean) {
        dateIdeaDao.updateBucketListStatus(ideaId, isSaved)
        if (isSaved) addCoupleXp(20)
    }

    suspend fun addSong(song: PlaylistSongEntity) {
        playlistDao.insertSong(song)
        addCoupleXp(25)
    }

    suspend fun addMemory(memory: MemoryEntity) {
        memoryDao.insertMemory(memory)
        addCoupleXp(40)
    }

    suspend fun addMemoryHeart(memoryId: Int) {
        memoryDao.addHeart(memoryId)
        addCoupleXp(5)
    }

    suspend fun recordGameWin(gameKey: String, isPartnerAWinner: Boolean) {
        val aWin = if (isPartnerAWinner) 1 else 0
        val bWin = if (!isPartnerAWinner) 1 else 0
        gameScoreDao.recordGameWin(gameKey, aWin, bWin)
        addCoupleXp(35)
    }

    fun updateCycleSharingMode(mode: CycleSharingMode) {
        _cycleStatus.value = _cycleStatus.value.copy(sharingMode = mode)
    }

    fun setCycleBiometricLocked(locked: Boolean) {
        _cycleStatus.value = _cycleStatus.value.copy(isBiometricLocked = locked)
    }

    fun updateCyclePhase(phase: CyclePhase, day: Int) {
        val nudge = when (phase) {
            CyclePhase.MENSTRUAL -> "Gentle comfort day: a warm soothing tea, their favorite comfort movie, and extra sweet voice notes will mean the world. 🍵🍫"
            CyclePhase.FOLLICULAR -> "High energy and optimism! Wonderful time for deep planning of your next reunion or a playful word duel. ✨"
            CyclePhase.OVULATORY -> "Peak glow and magnetic warmth! Schedule a late evening video date or trade handwritten digital letters. 💖"
            CyclePhase.LUTEAL -> "Rest and tender understanding: gentle check-ins, zero pressure, and reminding them how much they are cherished. 🌙"
        }
        _cycleStatus.value = _cycleStatus.value.copy(
            currentPhase = phase,
            dayOfCycle = day,
            partnerNudge = nudge
        )
    }

    fun addSavings(amount: Int) {
        _savingsSaved.value = (_savingsSaved.value + amount).coerceAtMost(_savingsGoal.value)
        addCoupleXp(30)
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
    }

    fun setFamilyGlanceShield(enabled: Boolean) {
        _isFamilyGlanceShield.value = enabled
    }
}
