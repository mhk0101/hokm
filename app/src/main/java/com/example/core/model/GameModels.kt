package com.example.core.model

enum class GamePhase {
    WAITING_FOR_PLAYERS,
    SELECTING_HAKEM,
    HAKEM_SELECTED,
    SELECTING_TRUMP,
    DEALING_REMAINING,
    PLAYING,
    TRICK_RESOLVED,
    HAND_COMPLETED,
    MATCH_COMPLETED
}

enum class GameMode(val persianTitle: String, val isRanked: Boolean) {
    RANKED("بازی رقابتی", true),
    CASUAL("بازی دوستانه", false)
}

data class PlayerSlot(
    val seatIndex: Int = 0,
    val userId: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val rating: Int = 1000,
    val isReady: Boolean = false,
    val isConnected: Boolean = true,
    val cardCount: Int = 0,
    val isHakem: Boolean = false
) {
    val team: Int get() = if (seatIndex % 2 == 0) 0 else 1 // Team 0 = Seat 0 & 2; Team 1 = Seat 1 & 3
}

data class PlayedCard(
    val seatIndex: Int = 0,
    val cardId: String = "",
    val timestamp: Long = 0L,
    val actionId: String = ""
) {
    val card: Card? get() = Card.fromId(cardId)
}

data class CurrentTrick(
    val trickIndex: Int = 1,
    val leadSuit: Suit? = null,
    val playedCards: List<PlayedCard> = emptyList(),
    val winnerSeatIndex: Int? = null
)

data class HandResult(
    val handNumber: Int = 1,
    val teamATricksWon: Int = 0,
    val teamBTricksWon: Int = 0,
    val winningTeam: Int = 0,
    val pointsAwarded: Int = 1, // 1 normal, 2 Kot, 3 Hakem-Kot
    val isKot: Boolean = false,
    val isHakemKot: Boolean = false
)

data class HokmGameState(
    val gameId: String = "",
    val mode: GameMode = GameMode.RANKED,
    val phase: GamePhase = GamePhase.WAITING_FOR_PLAYERS,
    val players: List<PlayerSlot> = emptyList(),
    val hakemSeatIndex: Int = 0,
    val trumpSuit: Suit? = null,
    val currentTurnSeat: Int = 0,
    val teamAPoints: Int = 0, // Target typically 7 points
    val teamBPoints: Int = 0,
    val targetPoints: Int = 7,
    val teamATricksCurrentHand: Int = 0,
    val teamBTricksCurrentHand: Int = 0,
    val currentTrick: CurrentTrick = CurrentTrick(),
    val sequenceNumber: Long = 0L,
    val version: Long = 1L,
    val serverTimestamp: Long = System.currentTimeMillis(),
    val winnerTeam: Int? = null,
    val currentHandNumber: Int = 1,
    val activeRoomCode: String = "",
    val lastActionId: String = ""
)

data class GameEvent(
    val id: String = "",
    val gameId: String = "",
    val eventType: String = "",
    val userId: String = "",
    val seatIndex: Int = -1,
    val data: Map<String, String> = emptyMap(),
    val sequenceNumber: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)
