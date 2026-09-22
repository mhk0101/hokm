package com.example.core.model

data class FriendUser(
    val userId: String = "",
    val username: String = "",
    val avatarId: String = "avatar_1",
    val rating: Int = 1000,
    val isOnline: Boolean = false,
    val inGame: Boolean = false
)

enum class EmoteType(val symbol: String, val label: String) {
    THUMBS_UP("👍", "عالی"),
    CLAP("👏", "تشویق"),
    LAUGH("😂", "خنده"),
    FIRE("🔥", "آتش"),
    COOL("😎", "خفن"),
    SURPRISED("😮", "تعجب"),
    SAD("😢", "ناراحت"),
    GG("GG", "بازی خوبی بود")
}

data class ActiveEmote(
    val seatIndex: Int,
    val emote: EmoteType,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserReport(
    val reportId: String = "",
    val reporterId: String = "",
    val targetUserId: String = "",
    val targetUsername: String = "",
    val gameId: String = "",
    val reason: String = "", // "تقلب", "توهین و فحاشی", "نام کاربری نامناسب", "آزار و اذیت", "سایر"
    val description: String = "",
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED"
    val createdAt: Long = System.currentTimeMillis()
)

data class AdminLog(
    val logId: String = "",
    val adminId: String = "",
    val targetUserId: String = "",
    val action: String = "",
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
