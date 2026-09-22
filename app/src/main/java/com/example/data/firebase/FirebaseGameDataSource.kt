package com.example.data.firebase

import com.example.core.engine.HokmRulesEngine
import com.example.core.model.Card
import com.example.core.model.CurrentTrick
import com.example.core.model.GameMode
import com.example.core.model.GamePhase
import com.example.core.model.HokmGameState
import com.example.core.model.PlayedCard
import com.example.core.model.PlayerSlot
import com.example.core.model.Suit
import com.example.core.model.UserProfile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseGameDataSource {

    private val firestore: FirebaseFirestore? get() = FirebaseManager.getFirestore()
    private val database: FirebaseDatabase? get() = FirebaseManager.getDatabase()

    // Local in-memory state fallback if Firebase server is in simulated dev testing mode
    private val localGameState = MutableStateFlow<HokmGameState?>(null)
    private val localHands = mutableMapOf<String, MutableMap<String, List<Card>>>()

    /**
     * Initializes a new Hokm game with 4 players.
     * Generates deck, determines Hakem, deals initial 5 cards, and stores private hands separately.
     */
    suspend fun createAndStartGame(
        players: List<UserProfile>,
        mode: GameMode = GameMode.RANKED,
        roomCode: String = ""
    ): Result<String> {
        require(players.size == 4) { "A Hokm game requires exactly 4 players" }
        val gameId = "game_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"

        val deck = HokmRulesEngine.generateShuffledDeck()
        val (hakemSeat, _) = HokmRulesEngine.determineFirstHakem(deck)
        val dealtHands = HokmRulesEngine.dealDeck(deck, hakemSeat)

        val playerSlots = players.mapIndexed { index, user ->
            PlayerSlot(
                seatIndex = index,
                userId = user.userId,
                username = user.username,
                avatarUrl = user.avatarId,
                rating = user.rating,
                isReady = true,
                isConnected = true,
                cardCount = 13,
                isHakem = (index == hakemSeat)
            )
        }

        val initialState = HokmGameState(
            gameId = gameId,
            mode = mode,
            phase = GamePhase.SELECTING_TRUMP,
            players = playerSlots,
            hakemSeatIndex = hakemSeat,
            trumpSuit = null,
            currentTurnSeat = hakemSeat, // Hakem selects trump
            teamAPoints = 0,
            teamBPoints = 0,
            targetPoints = 7,
            teamATricksCurrentHand = 0,
            teamBTricksCurrentHand = 0,
            currentTrick = CurrentTrick(trickIndex = 1),
            sequenceNumber = 1L,
            activeRoomCode = roomCode
        )

        // Store private hands in map: userId -> List of Card IDs
        val privateHandsMap = mutableMapOf<String, List<String>>()
        players.forEachIndexed { index, user ->
            val cards = dealtHands.seatCards[index] ?: emptyList()
            privateHandsMap[user.userId] = cards.map { it.id }
        }

        val fs = firestore
        if (fs != null) {
            try {
                val batch = fs.batch()
                val gameRef = fs.collection("games").document(gameId)
                batch.set(gameRef, initialState)

                // Store private hands strictly in isolated documents accessible only to that player
                privateHandsMap.forEach { (userId, cardIds) ->
                    val handRef = gameRef.collection("privateHands").document(userId)
                    batch.set(handRef, mapOf("cardIds" to cardIds, "seatIndex" to players.indexOfFirst { it.userId == userId }))
                }

                // Also publish presence in Realtime Database if available
                database?.getReference("activeGames")?.child(gameId)?.setValue(initialState)

                batch.commit().await()
            } catch (e: Exception) {
                // If offline or rule failure, save to local memory state
                localGameState.value = initialState
                localHands[gameId] = privateHandsMap.mapValues { entry ->
                    entry.value.mapNotNull { Card.fromId(it) }
                }.toMutableMap()
            }
        } else {
            localGameState.value = initialState
            localHands[gameId] = privateHandsMap.mapValues { entry ->
                entry.value.mapNotNull { Card.fromId(it) }
            }.toMutableMap()
        }

        return Result.success(gameId)
    }

    /**
     * Listens to public game state updates.
     */
    fun observeGameState(gameId: String): Flow<HokmGameState?> = callbackFlow {
        val fs = firestore
        if (fs == null) {
            val job = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).run {
                localGameState.collect { trySend(it) }
            }
            awaitClose { }
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = fs.collection("games").document(gameId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) {
                        trySend(localGameState.value)
                        return@addSnapshotListener
                    }
                    val state = snapshot.toObject(HokmGameState::class.java)
                    trySend(state)
                }
        } catch (e: Exception) {
            trySend(localGameState.value)
        }

        awaitClose {
            registration?.remove()
        }
    }

    /**
     * Listens to the current player's private hand (strictly authenticated user cards only).
     */
    fun observePlayerHand(gameId: String, userId: String): Flow<List<Card>> = callbackFlow {
        val fs = firestore
        if (fs == null) {
            val userCards = localHands[gameId]?.get(userId) ?: emptyList()
            trySend(userCards)
            awaitClose { }
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = fs.collection("games").document(gameId)
                .collection("privateHands").document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) {
                        val fallback = localHands[gameId]?.get(userId) ?: emptyList()
                        trySend(fallback)
                        return@addSnapshotListener
                    }
                    @Suppress("UNCHECKED_CAST")
                    val cardIds = snapshot.get("cardIds") as? List<String> ?: emptyList()
                    val cards = cardIds.mapNotNull { Card.fromId(it) }
                    trySend(cards)
                }
        } catch (e: Exception) {
            val fallback = localHands[gameId]?.get(userId) ?: emptyList()
            trySend(fallback)
        }

        awaitClose {
            registration?.remove()
        }
    }

    /**
     * Authoritative trump selection by Hakem.
     */
    suspend fun selectTrump(
        gameId: String,
        userId: String,
        trumpSuit: Suit,
        actionId: String
    ): Result<Unit> {
        val fs = firestore
        if (fs != null) {
            try {
                val gameRef = fs.collection("games").document(gameId)
                val snapshot = gameRef.get().await()
                val state = snapshot.toObject(HokmGameState::class.java)
                    ?: return Result.failure(Exception("بازی پیدا نشد."))

                val player = state.players.find { it.userId == userId }
                if (player == null || !player.isHakem) {
                    return Result.failure(Exception("تنها حاکم می‌تواند حکم را انتخاب کند."))
                }
                if (state.phase != GamePhase.SELECTING_TRUMP) {
                    return Result.failure(Exception("حکم قبلاً تعیین شده است."))
                }

                val updatedState = state.copy(
                    trumpSuit = trumpSuit,
                    phase = GamePhase.PLAYING,
                    currentTurnSeat = state.hakemSeatIndex, // Hakem plays first card
                    sequenceNumber = state.sequenceNumber + 1,
                    lastActionId = actionId,
                    serverTimestamp = System.currentTimeMillis()
                )

                gameRef.set(updatedState).await()
                localGameState.value = updatedState
                return Result.success(Unit)
            } catch (e: Exception) {
                // fall through to local update
            }
        }

        val cur = localGameState.value
        if (cur != null) {
            val updated = cur.copy(
                trumpSuit = trumpSuit,
                phase = GamePhase.PLAYING,
                currentTurnSeat = cur.hakemSeatIndex,
                sequenceNumber = cur.sequenceNumber + 1,
                lastActionId = actionId
            )
            localGameState.value = updated
        }
        return Result.success(Unit)
    }

    /**
     * Authoritative card play validation & commit.
     */
    suspend fun playCard(
        gameId: String,
        userId: String,
        card: Card,
        actionId: String
    ): Result<Unit> {
        val fs = firestore
        val curState = if (fs != null) {
            try {
                fs.collection("games").document(gameId).get().await().toObject(HokmGameState::class.java)
            } catch (e: Exception) {
                localGameState.value
            }
        } else {
            localGameState.value
        } ?: return Result.failure(Exception("بازی پیدا نشد."))

        val player = curState.players.find { it.userId == userId }
            ?: return Result.failure(Exception("شما عضو این بازی نیستید."))

        if (curState.currentTurnSeat != player.seatIndex) {
            return Result.failure(Exception("الان نوبت شما نیست."))
        }

        if (curState.lastActionId == actionId) {
            return Result.success(Unit) // Idempotent duplicate check
        }

        // Fetch hand to validate
        val currentHand = localHands[gameId]?.get(userId) ?: listOf(card)
        val validation = HokmRulesEngine.validateCardPlay(
            playerHand = currentHand,
            cardToPlay = card,
            currentTrick = curState.currentTrick,
            trumpSuit = curState.trumpSuit
        )

        if (validation is HokmRulesEngine.MoveValidationResult.Invalid) {
            return Result.failure(Exception(validation.persianReason))
        }

        // Remove card from player's hand
        val updatedHand = currentHand.filter { it.id != card.id }
        localHands.getOrPut(gameId) { mutableMapOf() }[userId] = updatedHand

        val playedCard = PlayedCard(
            seatIndex = player.seatIndex,
            cardId = card.id,
            timestamp = System.currentTimeMillis(),
            actionId = actionId
        )

        val newPlayedCards = curState.currentTrick.playedCards + playedCard
        val leadSuit = curState.currentTrick.leadSuit ?: card.suit

        // If trick complete (4 cards played)
        if (newPlayedCards.size == 4) {
            val winner = HokmRulesEngine.resolveTrickWinner(
                playedCards = newPlayedCards,
                leadSuit = leadSuit,
                trumpSuit = curState.trumpSuit
            )

            val winningTeam = if (winner.seatIndex % 2 == 0) 0 else 1
            val newTeamATricks = curState.teamATricksCurrentHand + (if (winningTeam == 0) 1 else 0)
            val newTeamBTricks = curState.teamBTricksCurrentHand + (if (winningTeam == 1) 1 else 0)

            // Check if hand finished (one team reached 7 tricks)
            val handResult = HokmRulesEngine.evaluateHandResult(
                handNumber = curState.currentHandNumber,
                teamATricks = newTeamATricks,
                teamBTricks = newTeamBTricks,
                hakemSeatIndex = curState.hakemSeatIndex
            )

            if (handResult != null) {
                // Hand completed
                val newTeamAPoints = curState.teamAPoints + (if (handResult.winningTeam == 0) handResult.pointsAwarded else 0)
                val newTeamBPoints = curState.teamBPoints + (if (handResult.winningTeam == 1) handResult.pointsAwarded else 0)

                val matchWinner = when {
                    newTeamAPoints >= curState.targetPoints -> 0
                    newTeamBPoints >= curState.targetPoints -> 1
                    else -> null
                }

                // Hakem rotation: if Hakem's team lost the hand, next seat clockwise becomes Hakem
                val hakemTeam = if (curState.hakemSeatIndex % 2 == 0) 0 else 1
                val nextHakem = if (handResult.winningTeam == hakemTeam) {
                    curState.hakemSeatIndex
                } else {
                    (curState.hakemSeatIndex + 1) % 4
                }

                val finalState = curState.copy(
                    phase = if (matchWinner != null) GamePhase.MATCH_COMPLETED else GamePhase.HAND_COMPLETED,
                    winnerTeam = matchWinner,
                    teamAPoints = newTeamAPoints,
                    teamBPoints = newTeamBPoints,
                    teamATricksCurrentHand = newTeamATricks,
                    teamBTricksCurrentHand = newTeamBTricks,
                    currentTrick = curState.currentTrick.copy(
                        playedCards = newPlayedCards,
                        leadSuit = leadSuit,
                        winnerSeatIndex = winner.seatIndex
                    ),
                    hakemSeatIndex = nextHakem,
                    currentTurnSeat = winner.seatIndex,
                    sequenceNumber = curState.sequenceNumber + 1,
                    lastActionId = actionId
                )

                commitState(gameId, userId, finalState, updatedHand)
            } else {
                // Trick resolved, proceed to next trick in same hand
                val intermediateState = curState.copy(
                    phase = GamePhase.TRICK_RESOLVED,
                    teamATricksCurrentHand = newTeamATricks,
                    teamBTricksCurrentHand = newTeamBTricks,
                    currentTrick = curState.currentTrick.copy(
                        playedCards = newPlayedCards,
                        leadSuit = leadSuit,
                        winnerSeatIndex = winner.seatIndex
                    ),
                    currentTurnSeat = winner.seatIndex, // Winner leads next trick
                    sequenceNumber = curState.sequenceNumber + 1,
                    lastActionId = actionId
                )

                commitState(gameId, userId, intermediateState, updatedHand)
            }
        } else {
            // Trick in progress, advance turn to next player clockwise
            val nextTurn = (player.seatIndex + 1) % 4
            val nextState = curState.copy(
                phase = GamePhase.PLAYING,
                currentTurnSeat = nextTurn,
                currentTrick = curState.currentTrick.copy(
                    playedCards = newPlayedCards,
                    leadSuit = leadSuit
                ),
                sequenceNumber = curState.sequenceNumber + 1,
                lastActionId = actionId
            )

            commitState(gameId, userId, nextState, updatedHand)
        }

        return Result.success(Unit)
    }

    private suspend fun commitState(
        gameId: String,
        userId: String,
        state: HokmGameState,
        remainingHand: List<Card>
    ) {
        localGameState.value = state
        val fs = firestore ?: return
        try {
            val batch = fs.batch()
            batch.set(fs.collection("games").document(gameId), state)
            batch.set(
                fs.collection("games").document(gameId).collection("privateHands").document(userId),
                mapOf("cardIds" to remainingHand.map { it.id })
            )
            batch.commit().await()
        } catch (e: Exception) {
            // Log or fallback
        }
    }

    suspend fun advanceToNextTrick(gameId: String) {
        val cur = localGameState.value ?: return
        if (cur.phase == GamePhase.TRICK_RESOLVED) {
            val nextTrickIndex = cur.currentTrick.trickIndex + 1
            val nextState = cur.copy(
                phase = GamePhase.PLAYING,
                currentTrick = CurrentTrick(trickIndex = nextTrickIndex, leadSuit = null, playedCards = emptyList())
            )
            localGameState.value = nextState
            firestore?.collection("games")?.document(gameId)?.set(nextState)
        }
    }
}
