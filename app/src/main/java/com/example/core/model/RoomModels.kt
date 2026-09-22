package com.example.core.model

enum class RoomStatus {
    WAITING,
    STARTING,
    IN_GAME,
    FINISHED
}

data class RoomSeat(
    val seatIndex: Int = 0,
    val userId: String? = null,
    val username: String? = null,
    val avatarId: String = "avatar_1",
    val rating: Int = 1000,
    val isReady: Boolean = false,
    val isHost: Boolean = false
) {
    val isOccupied: Boolean get() = userId != null
}

data class PrivateRoom(
    val roomCode: String = "",
    val hostUserId: String = "",
    val status: RoomStatus = RoomStatus.WAITING,
    val seats: List<RoomSeat> = List(4) { RoomSeat(seatIndex = it) },
    val gameId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val occupiedCount: Int get() = seats.count { it.isOccupied }
    val allSeatsReady: Boolean get() = seats.size == 4 && seats.all { it.isOccupied && (it.isReady || it.isHost) }
}
