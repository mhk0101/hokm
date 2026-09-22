package com.example.core.engine

import com.example.core.model.Card
import com.example.core.model.CurrentTrick
import com.example.core.model.HandResult
import com.example.core.model.PlayedCard
import com.example.core.model.Suit
import java.util.Random

object HokmRulesEngine {

    /**
     * Creates and shuffles a standard 52-card deck.
     */
    fun generateShuffledDeck(seed: Long? = null): List<Card> {
        val deck = Card.createDeck().toMutableList()
        val random = if (seed != null) Random(seed) else Random()
        deck.shuffle(random)
        return deck
    }

    /**
     * Fair Hakem selection:
     * Deals cards until the first Ace appears. The seat receiving the first Ace becomes the Hakem.
     */
    fun determineFirstHakem(shuffledDeck: List<Card>): Pair<Int, Card> {
        for (i in shuffledDeck.indices) {
            val card = shuffledDeck[i]
            if (card.rank.value == 14) { // Ace
                val seat = i % 4
                return Pair(seat, card)
            }
        }
        return Pair(0, shuffledDeck.first())
    }

    /**
     * Splits 52 cards into 4 hands of 13 cards each.
     * Hakem receives their initial 5 cards first to choose the trump suit.
     */
    data class DealtHands(
        val seatCards: Map<Int, List<Card>>,
        val hakemFirstFive: List<Card>
    )

    fun dealDeck(deck: List<Card>, hakemSeatIndex: Int): DealtHands {
        require(deck.size == 52) { "Deck must have exactly 52 cards" }
        val hands = mutableMapOf<Int, MutableList<Card>>()
        for (seat in 0..3) {
            hands[seat] = mutableListOf()
        }

        // Deal 5 cards to each player starting with Hakem
        for (offset in 0..3) {
            val seat = (hakemSeatIndex + offset) % 4
            val start = offset * 5
            hands[seat]?.addAll(deck.subList(start, start + 5))
        }

        val hakemFirstFive = hands[hakemSeatIndex]?.toList() ?: emptyList()

        // Deal remaining 8 cards (4 cards then 4 cards to each)
        var cursor = 20
        for (round in 0..1) {
            for (offset in 0..3) {
                val seat = (hakemSeatIndex + offset) % 4
                hands[seat]?.addAll(deck.subList(cursor, cursor + 4))
                cursor += 4
            }
        }

        return DealtHands(
            seatCards = hands.mapValues { it.value.sortedDescending() },
            hakemFirstFive = hakemFirstFive.sortedDescending()
        )
    }

    sealed class MoveValidationResult {
        object Valid : MoveValidationResult()
        data class Invalid(val persianReason: String) : MoveValidationResult()
    }

    /**
     * Validates whether a card can be played in the current trick.
     */
    fun validateCardPlay(
        playerHand: List<Card>,
        cardToPlay: Card,
        currentTrick: CurrentTrick,
        trumpSuit: Suit?
    ): MoveValidationResult {
        if (!playerHand.contains(cardToPlay)) {
            return MoveValidationResult.Invalid("شما این کارت را در دست خود ندارید.")
        }

        if (currentTrick.playedCards.any { it.cardId == cardToPlay.id }) {
            return MoveValidationResult.Invalid("این کارت قبلاً در این دست بازی شده است.")
        }

        val leadSuit = currentTrick.leadSuit
        if (leadSuit != null) {
            val hasLeadSuit = playerHand.any { it.suit == leadSuit }
            if (hasLeadSuit && cardToPlay.suit != leadSuit) {
                return MoveValidationResult.Invalid("شما باید از همان خال بازی کنید.")
            }
        }

        return MoveValidationResult.Valid
    }

    /**
     * Resolves the winning card and seat of a 4-card trick.
     */
    fun resolveTrickWinner(
        playedCards: List<PlayedCard>,
        leadSuit: Suit,
        trumpSuit: Suit?
    ): PlayedCard {
        require(playedCards.isNotEmpty()) { "Cannot resolve winner of empty trick" }

        val trumpCards = if (trumpSuit != null) {
            playedCards.filter { it.card?.suit == trumpSuit }
        } else emptyList()

        return if (trumpCards.isNotEmpty()) {
            // Highest trump wins
            trumpCards.maxByOrNull { it.card?.rank?.value ?: 0 } ?: playedCards.first()
        } else {
            // Highest card of the lead suit wins
            val leadSuitCards = playedCards.filter { it.card?.suit == leadSuit }
            leadSuitCards.maxByOrNull { it.card?.rank?.value ?: 0 } ?: playedCards.first()
        }
    }

    /**
     * Calculates the points awarded when a hand reaches 7 tricks.
     * Team 0 = Seats 0 & 2; Team 1 = Seats 1 & 3
     */
    fun evaluateHandResult(
        handNumber: Int,
        teamATricks: Int,
        teamBTricks: Int,
        hakemSeatIndex: Int
    ): HandResult? {
        val winningTeam = when {
            teamATricks >= 7 -> 0
            teamBTricks >= 7 -> 1
            else -> return null // Hand not finished yet
        }

        val hakemTeam = if (hakemSeatIndex % 2 == 0) 0 else 1
        val loserTricks = if (winningTeam == 0) teamBTricks else teamATricks

        val isKot = loserTricks == 0
        val isHakemKot = isKot && (winningTeam != hakemTeam) // Opponents defeated Hakem's team 7-0!

        val points = when {
            isHakemKot -> 3
            isKot -> 2
            else -> 1
        }

        return HandResult(
            handNumber = handNumber,
            teamATricksWon = teamATricks,
            teamBTricksWon = teamBTricks,
            winningTeam = winningTeam,
            pointsAwarded = points,
            isKot = isKot,
            isHakemKot = isHakemKot
        )
    }

    /**
     * Standard Elo Rating Calculation
     * K-Factor 32
     */
    data class RatingResult(
        val ratingBefore: Int,
        val ratingAfter: Int,
        val ratingChange: Int
    )

    fun calculateEloChange(
        teamPlayerRating: Int,
        opponentAverageRating: Int,
        didWin: Boolean,
        kFactor: Int = 32
    ): RatingResult {
        val expectedScore = 1.0 / (1.0 + Math.pow(10.0, (opponentAverageRating - teamPlayerRating) / 400.0))
        val actualScore = if (didWin) 1.0 else 0.0
        val change = Math.round(kFactor * (actualScore - expectedScore)).toInt()
        val newRating = Math.max(100, teamPlayerRating + change)
        return RatingResult(
            ratingBefore = teamPlayerRating,
            ratingAfter = newRating,
            ratingChange = change
        )
    }

    /**
     * XP Calculation
     */
    fun calculateXpReward(didWin: Boolean, isKot: Boolean, isHakemKot: Boolean): Long {
        if (!didWin) return 40L
        return when {
            isHakemKot -> 300L
            isKot -> 220L
            else -> 150L
        }
    }
}
