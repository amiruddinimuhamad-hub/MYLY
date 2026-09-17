package com.example.data.model

enum class QuoteMood(val label: String, val emoji: String) {
    SWEET("Sweet", "🍯"),
    FUNNY("Funny", "😆"),
    DEEP("Deep", "🌌"),
    CHEESY("Cheesy", "🧀")
}

data class DailyQuote(
    val id: String,
    val text: String,
    val authorOrContext: String,
    val mood: QuoteMood,
    val dayNumber: Int = 214
)

enum class CyclePhase(val displayName: String, val durationDays: String, val icon: String, val colorHex: Long) {
    MENSTRUAL("Menstrual Phase", "Days 1-5", "🩸", 0xFFE57373),
    FOLLICULAR("Follicular Phase", "Days 6-13", "🌱", 0xFF81C784),
    OVULATORY("Ovulatory Window", "Days 14-16", "✨", 0xFFFFB74D),
    LUTEAL("Luteal Phase", "Days 17-28", "🌙", 0xFFBA68C8)
}

enum class CycleSharingMode {
    PRIVATE,      // Hidden from partner
    PHASE_ONLY,   // Partner only sees phase name & gentle tips
    FULL_DETAIL   // Partner can see symptoms & comfort notes
}

data class CycleStatus(
    val currentPhase: CyclePhase = CyclePhase.FOLLICULAR,
    val dayOfCycle: Int = 9,
    val totalCycleLength: Int = 28,
    val fertileWindowStartDay: Int = 12,
    val fertileWindowEndDay: Int = 16,
    val symptoms: List<String> = listOf("High Energy", "Creative", "Craving Boba"),
    val partnerNudge: String = "She is in her high-energy creative phase! Perfect time for a fun virtual game night or planning your next trip itinerary. 💡✈️",
    val sharingMode: CycleSharingMode = CycleSharingMode.PHASE_ONLY,
    val isBiometricLocked: Boolean = true
)

data class CoupleProfile(
    val partnerAName: String = "Alex",
    val partnerBName: String = "Sam",
    val partnerACity: String = "Tokyo",
    val partnerBCity: String = "London",
    val partnerATimeZone: String = "GMT+9",
    val partnerBTimeZone: String = "GMT+1",
    val distanceKm: Int = 9560,
    val currentActiveUser: String = "Sam", // Toggle between viewing as Alex or Sam
    val daysTogether: Int = 214,
    val nextVisitDateString: String = "Oct 12, 2026",
    val daysUntilVisit: Int = 14,
    val levelTitle: String = "Cosmic Stargazers",
    val currentXp: Int = 780,
    val maxXp: Int = 1000,
    val currentLevel: Int = 8
)

data class StreakInfo(
    val currentStreak: Int = 48,
    val partnerACheckedIn: Boolean = true,
    val partnerBCheckedIn: Boolean = false,
    val hasFreezeAvailable: Boolean = true,
    val isFreezeActive: Boolean = false,
    val longestStreak: Int = 62
)

data class TriviaQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val partnerFunFact: String
)

data class WordDuelRound(
    val scrambled: String,
    val targetWord: String,
    val hint: String
)

data class ThisOrThatOption(
    val optionA: String,
    val optionB: String,
    val partnerAChoice: Int? = 0, // 0 for A, 1 for B
    val partnerBChoice: Int? = null
)
