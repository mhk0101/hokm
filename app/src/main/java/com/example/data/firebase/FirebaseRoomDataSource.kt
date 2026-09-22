package com.example.data.firebase

import com.example.core.model.GameMode
import com.example.core.model.PrivateRoom
import com.example.core.model.RoomSeat
import com.example.core.model.RoomStatus
import com.example.core.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirebaseRoomDataSource(
    private val gameDataSource: FirebaseGameDataSource
) {
    private val firestore: FirebaseFirestore? get() = FirebaseManager.getFirestore()

    private val localRoomState = MutableStateFlow<PrivateRoom?>(null)

    fun observeRoom(roomCode: String): Flow<PrivateRoom?> = callbackFlow {
        val fs = firestore
        if (fs == null) {
            localRoomState.collect { trySend(it) }
            awaitClose { }
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = fs.collection("rooms").document(roomCode)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) {
                        trySend(localRoomState.value)
                        return@addSnapshotListener
                    }
                    val room = snapshot.toObject(PrivateRoom::class.java)
                    trySend(room)
                }
        } catch (e: Exception) {
            trySend(localRoomState.value)
        }

        awaitClose {
            registration?.remove()
        }
    }

    suspend fun createRoom(hostUser: UserProfile): Result<PrivateRoom> {
        val roomCode = (100000 + Random.nextInt(900000)).toString()
        val initialSeats = List(4) { index ->
            if (index == 0) {
                RoomSeat(
                    seatIndex = 0,
                    userId = hostUser.userId,
                    username = hostUser.username,
                    avatarId = hostUser.avatarId,
                    rating = hostUser.rating,
                    isReady = true,
                    isHost = true
                )
            } else {
                RoomSeat(seatIndex = index)
            }
        }

        val room = PrivateRoom(
            roomCode = roomCode,
            hostUserId = hostUser.userId,
            status = RoomStatus.WAITING,
            seats = initialSeats,
            createdAt = System.currentTimeMillis()
        )

        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("rooms").document(roomCode).set(room).await()
            } catch (e: Exception) {
                localRoomState.value = room
            }
        } else {
            localRoomState.value = room
        }

        return Result.success(room)
    }

    suspend fun joinRoom(roomCode: String, user: UserProfile): Result<PrivateRoom> {
        val fs = firestore
        val room = if (fs != null) {
            try {
                fs.collection("rooms").document(roomCode).get().await().toObject(PrivateRoom::class.java)
            } catch (e: Exception) {
                localRoomState.value
            }
        } else {
            localRoomState.value
        } ?: return Result.failure(Exception("اتاق پیدا نشد."))

        if (room.status != RoomStatus.WAITING) {
            return Result.failure(Exception("این بازی شروع شده یا به پایان رسیده است."))
        }

        val alreadyInSeat = room.seats.find { it.userId == user.userId }
        if (alreadyInSeat != null) {
            return Result.success(room)
        }

        val emptySeat = room.seats.find { !it.isOccupied }
            ?: return Result.failure(Exception("اتاق تکمیل شده است."))

        val updatedSeats = room.seats.map { seat ->
            if (seat.seatIndex == emptySeat.seatIndex) {
                seat.copy(
                    userId = user.userId,
                    username = user.username,
                    avatarId = user.avatarId,
                    rating = user.rating,
                    isReady = false,
                    isHost = false
                )
            } else seat
        }

        val updatedRoom = room.copy(seats = updatedSeats)
        if (fs != null) {
            fs.collection("rooms").document(roomCode).set(updatedRoom).await()
        } else {
            localRoomState.value = updatedRoom
        }

        return Result.success(updatedRoom)
    }

    suspend fun toggleReady(roomCode: String, userId: String): Result<Unit> {
        val fs = firestore
        val room = if (fs != null) {
            try {
                fs.collection("rooms").document(roomCode).get().await().toObject(PrivateRoom::class.java)
            } catch (e: Exception) {
                localRoomState.value
            }
        } else {
            localRoomState.value
        } ?: return Result.failure(Exception("اتاق پیدا نشد."))

        val updatedSeats = room.seats.map { seat ->
            if (seat.userId == userId && !seat.isHost) {
                seat.copy(isReady = !seat.isReady)
            } else seat
        }

        val updatedRoom = room.copy(seats = updatedSeats)
        if (fs != null) {
            fs.collection("rooms").document(roomCode).set(updatedRoom).await()
        } else {
            localRoomState.value = updatedRoom
        }

        return Result.success(Unit)
    }

    suspend fun startRoomGame(roomCode: String, hostUserId: String): Result<String> {
        val fs = firestore
        val room = if (fs != null) {
            try {
                fs.collection("rooms").document(roomCode).get().await().toObject(PrivateRoom::class.java)
            } catch (e: Exception) {
                localRoomState.value
            }
        } else {
            localRoomState.value
        } ?: return Result.failure(Exception("اتاق پیدا نشد."))

        if (room.hostUserId != hostUserId) {
            return Result.failure(Exception("فقط سازنده اتاق می‌تواند بازی را آغاز کند."))
        }

        if (room.occupiedCount < 4) {
            return Result.failure(Exception("برای شروع بازی باید هر ۴ صندلی پر باشند."))
        }

        val players = room.seats.mapNotNull { seat ->
            seat.userId?.let { uid ->
                UserProfile(
                    userId = uid,
                    username = seat.username ?: "بازیکن",
                    avatarId = seat.avatarId,
                    rating = seat.rating
                )
            }
        }

        if (players.size != 4) {
            return Result.failure(Exception("اطلاعات بازیکنان ناقص است."))
        }

        val gameResult = gameDataSource.createAndStartGame(
            players = players,
            mode = GameMode.CASUAL,
            roomCode = roomCode
        )

        return gameResult.map { gameId ->
            val finalRoom = room.copy(status = RoomStatus.IN_GAME, gameId = gameId)
            if (fs != null) {
                fs.collection("rooms").document(roomCode).set(finalRoom)
            } else {
                localRoomState.value = finalRoom
            }
            gameId
        }
    }

    suspend fun leaveRoom(roomCode: String, userId: String) {
        val fs = firestore
        val room = if (fs != null) {
            try {
                fs.collection("rooms").document(roomCode).get().await().toObject(PrivateRoom::class.java)
            } catch (e: Exception) {
                localRoomState.value
            }
        } else {
            localRoomState.value
        } ?: return

        if (room.hostUserId == userId) {
            // Delete room if host leaves
            fs?.collection("rooms")?.document(roomCode)?.delete()
            localRoomState.value = null
        } else {
            val updatedSeats = room.seats.map { seat ->
                if (seat.userId == userId) {
                    RoomSeat(seatIndex = seat.seatIndex)
                } else seat
            }
            val updatedRoom = room.copy(seats = updatedSeats)
            fs?.collection("rooms")?.document(roomCode)?.set(updatedRoom)
            localRoomState.value = updatedRoom
        }
    }
}
