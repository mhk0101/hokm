package com.example.core.model

enum class TableStyle {
    PERSIAN_CARPET,
    CLASSIC_GREEN,
    DARK_ELEGANT,
    WOODEN_TABLE,
    ROYAL_BLUE,
    NIGHT,
    MINIMAL
}

enum class CardBackStyle {
    CLASSIC_RED,
    ROYAL_BLUE,
    GOLD,
    BLACK,
    PERSIAN,
    MINIMAL
}

data class GameTheme(
    val id: String,
    val name: String,
    val description: String,
    val style: TableStyle,
    val accentColor: Long,
    val priceCoins: Long = 0L,
    val isPremium: Boolean = false,
    val isDefaultUnlocked: Boolean = false
) {
    companion object {
        val ALL_THEMES = listOf(
            GameTheme("persian_carpet", "فرش ایرانی", "نقوش اصیل شاه‌عباسی و حاشیه سنتی ایرانی", TableStyle.PERSIAN_CARPET, 0xFFB71C1C, 0, isDefaultUnlocked = true),
            GameTheme("classic_green", "سبز کلاسیک", "میز سبز مخمل حرفه‌ای مسابقات بین‌المللی", TableStyle.CLASSIC_GREEN, 0xFF1B5E20, 0, isDefaultUnlocked = true),
            GameTheme("dark_elegant", "مشکی اشرافی", "طراحی لوکس مشکی مات با خطوط طلایی", TableStyle.DARK_ELEGANT, 0xFFFFD700, 300),
            GameTheme("wooden_table", "میز چوبی", "رویه چوب گردوی دست‌ساز و نوستالژیک", TableStyle.WOODEN_TABLE, 0xFF8D6E63, 500),
            GameTheme("royal_blue", "آبی سلطنتی", "آبی کاربنی فاخر با نقش‌های فیروزه‌ای", TableStyle.ROYAL_BLUE, 0xFF1565C0, 700),
            GameTheme("night", "شب مهتابی", "فضای تیره ستاره‌ای با گرادیان سرمه‌ای عمیق", TableStyle.NIGHT, 0xFF6A1B9A, 900),
            GameTheme("minimal", "مینیمال ساده", "ترکیب ساده و تمرکز حداکثری روی کارت‌ها", TableStyle.MINIMAL, 0xFF607D8B, 1200)
        )
    }
}

data class CardBack(
    val id: String,
    val name: String,
    val description: String,
    val style: CardBackStyle,
    val primaryColor: Long,
    val secondaryColor: Long,
    val priceCoins: Long = 0L,
    val isDefaultUnlocked: Boolean = false
) {
    companion object {
        val ALL_CARD_BACKS = listOf(
            CardBack("classic_red", "قرمز کلاسیک", "طرح کلاسیک لوزی زرشکی سنتی", CardBackStyle.CLASSIC_RED, 0xFFC62828, 0xFF8E0000, 0, isDefaultUnlocked = true),
            CardBack("royal_blue", "آبی سلطنتی", "طرح آبی یاقوتی سلطنتی", CardBackStyle.ROYAL_BLUE, 0xFF1565C0, 0xFF0D47A1, 200),
            CardBack("gold", "طلایی شاهانه", "نقش زرکوب برجسته اشرافی", CardBackStyle.GOLD, 0xFFFFD700, 0xFFFFA000, 500),
            CardBack("black", "مشکی مات", "طراحی کربنی مدرن و شیک", CardBackStyle.BLACK, 0xFF212121, 0xFF000000, 600),
            CardBack("persian", "اسلیمی ایرانی", "تذهیب فیروزه و طلای اصفهان", CardBackStyle.PERSIAN, 0xFF00897B, 0xFF004D40, 800),
            CardBack("minimal", "مینیمال مدرن", "خطوط هندسی خالص و یکدست", CardBackStyle.MINIMAL, 0xFF546E7A, 0xFF37474F, 1000)
        )
    }
}

data class CoinTransaction(
    val id: String = "",
    val userId: String = "",
    val amount: Long = 0L,
    val balanceBefore: Long = 0L,
    val balanceAfter: Long = 0L,
    val type: String = "REWARD", // "REWARD", "PURCHASE", "DAILY", "ADMIN"
    val reason: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class DailyRewardItem(
    val day: Int,
    val rewardTitle: String,
    val coins: Long,
    val xp: Long,
    val isClaimed: Boolean = false,
    val isAvailableToday: Boolean = false
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val maxProgress: Int,
    val xpReward: Long,
    val coinReward: Long,
    val isUnlocked: Boolean = false
)
