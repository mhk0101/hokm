package com.example

import com.example.core.engine.HokmRulesEngine
import com.example.core.model.Card
import com.example.core.model.CurrentTrick
import com.example.core.model.PlayedCard
import com.example.core.model.Rank
import com.example.core.model.Suit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HokmRulesEngineTest {

    @Test
    fun deckGeneration_containsExactly52UniqueCards() {
        val deck = HokmRulesEngine.generateShuffledDeck()
        assertEquals(52, deck.size)
        assertEquals(52, deck.distinctBy { it.id }.size)

        // 13 of each suit
        Suit.entries.forEach { suit ->
            assertEquals(13, deck.count { it.suit == suit })
        }
    }

    @Test
    fun dealing_deals13CardsToEachOf4Players() {
        val deck = HokmRulesEngine.generateShuffledDeck()
        val dealt = HokmRulesEngine.dealDeck(deck, hakemSeatIndex = 0)

        assertEquals(4, dealt.seatCards.size)
        dealt.seatCards.values.forEach { hand ->
            assertEquals(13, hand.size)
        }
        assertEquals(5, dealt.hakemFirstFive.size)
    }

    @Test
    fun moveValidation_leadSuitMustBeFollowedIfAvailable() {
        val leadCard = Card(Suit.HEARTS, Rank.TEN)
        val currentTrick = CurrentTrick(
            trickIndex = 0,
            leadSuit = Suit.HEARTS,
            playedCards = listOf(PlayedCard(0, leadCard.id))
        )

        // Hand has hearts and spades
        val playerHand = listOf(
            Card(Suit.HEARTS, Rank.TWO),
            Card(Suit.SPADES, Rank.ACE)
        )

        // Playing Spade when having Heart is illegal
        val illegalPlay = HokmRulesEngine.validateCardPlay(
            playerHand,
            Card(Suit.SPADES, Rank.ACE),
            currentTrick,
            trumpSuit = Suit.SPADES
        )
        assertTrue(illegalPlay is HokmRulesEngine.MoveValidationResult.Invalid)

        // Playing Heart is legal
        val legalPlay = HokmRulesEngine.validateCardPlay(
            playerHand,
            Card(Suit.HEARTS, Rank.TWO),
            currentTrick,
            trumpSuit = Suit.SPADES
        )
        assertTrue(legalPlay is HokmRulesEngine.MoveValidationResult.Valid)
    }

    @Test
    fun moveValidation_canPlayAnySuitWhenVoidOfLeadSuit() {
        val leadCard = Card(Suit.DIAMONDS, Rank.KING)
        val currentTrick = CurrentTrick(
            trickIndex = 0,
            leadSuit = Suit.DIAMONDS,
            playedCards = listOf(PlayedCard(0, leadCard.id))
        )

        // Player has no Diamonds
        val playerHand = listOf(
            Card(Suit.SPADES, Rank.TEN),
            Card(Suit.CLUBS, Rank.ACE)
        )

        val play = HokmRulesEngine.validateCardPlay(
            playerHand,
            Card(Suit.SPADES, Rank.TEN),
            currentTrick,
            trumpSuit = Suit.SPADES
        )
        assertTrue(play is HokmRulesEngine.MoveValidationResult.Valid)
    }

    @Test
    fun trickResolution_highestLeadSuitWinsWhenNoTrump() {
        val card0 = Card(Suit.CLUBS, Rank.SEVEN)
        val card1 = Card(Suit.CLUBS, Rank.JACK)
        val card2 = Card(Suit.CLUBS, Rank.ACE)
        val card3 = Card(Suit.HEARTS, Rank.KING)

        val played = listOf(
            PlayedCard(0, card0.id),
            PlayedCard(1, card1.id),
            PlayedCard(2, card2.id),
            PlayedCard(3, card3.id)
        )

        val winningPlayed = HokmRulesEngine.resolveTrickWinner(
            playedCards = played,
            leadSuit = Suit.CLUBS,
            trumpSuit = Suit.SPADES
        )
        assertEquals(2, winningPlayed.seatIndex) // Seat 2 with Ace of Clubs
    }

    @Test
    fun trickResolution_trumpCutsAndBeatsLeadSuit() {
        val card0 = Card(Suit.HEARTS, Rank.ACE)
        val card1 = Card(Suit.HEARTS, Rank.KING)
        val card2 = Card(Suit.SPADES, Rank.TWO) // Trump cut!
        val card3 = Card(Suit.HEARTS, Rank.QUEEN)

        val played = listOf(
            PlayedCard(0, card0.id),
            PlayedCard(1, card1.id),
            PlayedCard(2, card2.id),
            PlayedCard(3, card3.id)
        )

        val winningPlayed = HokmRulesEngine.resolveTrickWinner(
            playedCards = played,
            leadSuit = Suit.HEARTS,
            trumpSuit = Suit.SPADES
        )
        assertEquals(2, winningPlayed.seatIndex) // Seat 2 cut with 2 of Spades (trump)
    }

    @Test
    fun handResolution_detectsHakemKotAndKotProperly() {
        // Team B (Seat 1/3) gets 7 tricks, Team A (Seat 0 Hakem) gets 0 tricks -> Hakem Kot (3 points)
        val hakemKot = HokmRulesEngine.evaluateHandResult(
            handNumber = 1,
            teamATricks = 0,
            teamBTricks = 7,
            hakemSeatIndex = 0 // Team A is Hakem
        )
        assertNotNull(hakemKot)
        assertEquals(1, hakemKot?.winningTeam)
        assertEquals(3, hakemKot?.pointsAwarded)
        assertTrue(hakemKot?.isHakemKot == true)

        // Team A (Hakem) gets 7 tricks, Team B gets 0 tricks -> Normal Kot (2 points)
        val normalKot = HokmRulesEngine.evaluateHandResult(
            handNumber = 2,
            teamATricks = 7,
            teamBTricks = 0,
            hakemSeatIndex = 0
        )
        assertNotNull(normalKot)
        assertEquals(0, normalKot?.winningTeam)
        assertEquals(2, normalKot?.pointsAwarded)
        assertTrue(normalKot?.isKot == true)
        assertFalse(normalKot?.isHakemKot == true)

        // Normal 7-4 win -> 1 point
        val regular = HokmRulesEngine.evaluateHandResult(
            handNumber = 3,
            teamATricks = 7,
            teamBTricks = 4,
            hakemSeatIndex = 0
        )
        assertNotNull(regular)
        assertEquals(0, regular?.winningTeam)
        assertEquals(1, regular?.pointsAwarded)
        assertFalse(regular?.isKot == true)
        assertFalse(regular?.isHakemKot == true)
    }

    @Test
    fun eloCalculation_adjustsFairly() {
        val resultWin = HokmRulesEngine.calculateEloChange(
            teamPlayerRating = 1400,
            opponentAverageRating = 1600,
            didWin = true
        )
        assertTrue(resultWin.ratingChange > 20)
        assertEquals(1400 + resultWin.ratingChange, resultWin.ratingAfter)

        val resultLoss = HokmRulesEngine.calculateEloChange(
            teamPlayerRating = 1600,
            opponentAverageRating = 1400,
            didWin = false
        )
        assertTrue(resultLoss.ratingChange < -20)
    }
}
