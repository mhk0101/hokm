package com.example.core.model

enum class League(
    val englishName: String,
    val persianName: String,
    val minRating: Int,
    val maxRating: Int,
    val colorHex: Long
) {
    BRONZE("Bronze", "برنز", 0, 1199, 0xFFCD7F32),
    SILVER("Silver", "نقره", 1200, 1399, 0xFFC0C0C0),
    GOLD("Gold", "طلا", 1400, 1599, 0xFFFFD700),
    PLATINUM("Platinum", "پلاتین", 1600, 1799, 0xFFE5E4E2),
    DIAMOND("Diamond", "الماس", 1800, 1999, 0xFFB9F2FF),
    MASTER("Master", "استاد", 2000, 2299, 0xFFFF6F00),
    GRAND_MASTER("Grand Master", "استاد بزرگ", 2300, 9999, 0xFFD500F9);

    companion object {
        fun fromRating(rating: Int): League {
            return entries.find { rating in it.minRating..it.maxRating } ?: BRONZE
        }
    }
}

data class UserStats(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val kotsGiven: Int = 0,
    val hakemKotsGiven: Int = 0
) {
    val winRate: Int
        get() = if (gamesPlayed > 0) ((wins.toDouble() / gamesPlayed) * 100).toInt() else 0
}

data class UserProfile(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val avatarId: String = "avatar_1",
    val rating: Int = 1000,
    val xp: Long = 0L,
    val level: Int = 1,
    val coins: Long = 500L,
    val stats: UserStats = UserStats(),
    val currentSeasonId: String = "season_1",
    val activeThemeId: String = "persian_carpet",
    val activeCardBackId: String = "classic_red",
    val isBanned: Boolean = false,
    val isMuted: Boolean = false,
    val role: String = "user", // "user", "moderator", "admin", "super_admin"
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    val league: League get() = League.fromRating(rating)

    val xpForNextLevel: Long get() = level * 250L
    val currentLevelXpProgress: Float get() = (xp % 250L).toFloat() / 250f
}

data class Season(
    val id: String = "season_1",
    val name: String = "فصل اول: پادشاهی",
    val startDate: Long = 1735689600000L,
    val endDate: Long = 1743465600000L,
    val status: String = "ACTIVE"
)

data class LeaderboardEntry(
    val rank: Int = 1,
    val userId: String = "",
    val username: String = "",
    val rating: Int = 1000,
    val league: League = League.BRONZE,
    val wins: Int = 0,
    val avatarId: String = "avatar_1"
)
