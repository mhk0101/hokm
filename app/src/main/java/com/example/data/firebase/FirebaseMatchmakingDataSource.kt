package com.example.data.firebase

import com.example.core.model.GameMode
import com.example.core.model.UserProfile
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

sealed class MatchmakingStatus {
    object Idle : MatchmakingStatus()
    data class Searching(val elapsedSeconds: Int, val playersFound: Int) : MatchmakingStatus()
    data class Matched(val gameId: String) : MatchmakingStatus()
    data class Error(val message: String) : MatchmakingStatus()
}

class FirebaseMatchmakingDataSource(
    private val gameDataSource: FirebaseGameDataSource
) {
    private val firestore: FirebaseFirestore? get() = FirebaseManager.getFirestore()
    private val database: FirebaseDatabase? get() = FirebaseManager.getDatabase()

    private val _status = MutableStateFlow<MatchmakingStatus>(MatchmakingStatus.Idle)
    val status = _status.asStateFlow()

    private var activeQueueUserId: String? = null

    suspend fun enterQueue(user: UserProfile, mode: GameMode = GameMode.RANKED) {
        activeQueueUserId = user.userId
        _status.value = MatchmakingStatus.Searching(0, 1)

        val fs = firestore
        if (fs != null) {
            try {
                val queueRef = fs.collection("matchmakingQueue")
                // Register queue entry
                val entryData = mapOf(
                    "userId" to user.userId,
                    "username" to user.username,
                    "avatarId" to user.avatarId,
                    "rating" to user.rating,
                    "mode" to mode.name,
                    "createdAt" to System.currentTimeMillis()
                )
                queueRef.document(user.userId).set(entryData).await()

                // Check for available players in queue
                val snapshot = queueRef.whereEqualTo("mode", mode.name)
                    .orderBy("createdAt")
                    .limit(4)
                    .get().await()

                if (snapshot.size() == 4) {
                    val queuedPlayers = snapshot.documents.mapNotNull { doc ->
                        UserProfile(
                            userId = doc.getString("userId") ?: "",
                            username = doc.getString("username") ?: "بازیکن",
                            avatarId = doc.getString("avatarId") ?: "avatar_1",
                            rating = doc.getLong("rating")?.toInt() ?: 1000
                        )
                    }

                    // Remove from queue
                    val batch = fs.batch()
                    snapshot.documents.forEach { batch.delete(it.reference) }
                    batch.commit().await()

                    // Start game
                    val gameResult = gameDataSource.createAndStartGame(queuedPlayers, mode)
                    gameResult.onSuccess { gameId ->
                        _status.value = MatchmakingStatus.Matched(gameId)
                    }
                }
            } catch (e: Exception) {
                // If cloud matchmaking fails, notify user
                _status.value = MatchmakingStatus.Error(e.localizedMessage ?: "خطا در اتصال به صف بازی")
            }
        } else {
            _status.value = MatchmakingStatus.Error("فایربیس متصل نیست. برای بازی آنلاین لطفا فایل تنظیمات را اضافه کنید.")
        }
    }

    suspend fun leaveQueue() {
        val uid = activeQueueUserId ?: return
        firestore?.collection("matchmakingQueue")?.document(uid)?.delete()
        _status.value = MatchmakingStatus.Idle
        activeQueueUserId = null
    }
}
